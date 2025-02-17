package com.example.sankhya.service.impl;

import com.example.sankhya.controller.dto.PedidoDTO;
import com.example.sankhya.controller.dto.PedidoItensDTO;
import com.example.sankhya.domain.Cliente;
import com.example.sankhya.domain.Pedido;
import com.example.sankhya.domain.PedidoItens;
import com.example.sankhya.domain.Produto;
import com.example.sankhya.domain.enums.PedidoStatus;
import com.example.sankhya.exception.PedidoNaoEncontradoException;
import com.example.sankhya.exception.RegraNegocioException;
import com.example.sankhya.repository.ClienteRepository;
import com.example.sankhya.repository.PedidoItensRepository;
import com.example.sankhya.repository.PedidoRepository;
import com.example.sankhya.repository.ProdutoRepository;
import com.example.sankhya.service.PedidoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de pedidos, responsável por realizar operações como:
 * salvar pedidos, atualizar status, buscar informações de pedidos e gerar relatórios.
 */
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;

    private final ClienteRepository clienteRepository;

    private final ProdutoRepository produtoRepository;

    private final PedidoItensRepository pedidoItensRepository;

    private final Executor taskExecutor;

    /**
     * Salva um novo pedido no sistema.
     *
     * @param pedidoDTO Dados do pedido a ser salvo.
     * @return O objeto Pedido recém-criado.
     * @throws RegraNegocioException Caso o cliente informado não exista.
     */
    @Override
    @Transactional
    public Pedido save(PedidoDTO pedidoDTO) {
        Long codClient = pedidoDTO.getClienteId();
        Cliente cliente = clienteRepository.findById(codClient)
                .orElseThrow(() -> new RegraNegocioException("Codigo Cliente invalido"));

        Pedido pedido = new Pedido();
        pedido.setDataPedido(LocalDate.now());
        pedido.setTotal(pedidoDTO.getTotal());
        pedido.setCliente(cliente);
        pedido.setPedidoStatus(PedidoStatus.REALIZADO);
        Set<PedidoItens> pedidoItens = convertItems(pedido, pedidoDTO.getPedidoItens());
        pedidoRepository.save(pedido);
        pedidoItensRepository.saveAll(pedidoItens);
        pedido.setPedidoItens(pedidoItens);

        return pedido;
    }

    @Override
    public Optional<Pedido> informacoesPedido(Long idPedido) {

        return pedidoRepository.findById(idPedido);
    }

    /**
     * Atualiza o status do pedido.
     *
     * @param id ID do pedido a ser atualizado.
     * @param pedidoStatus Novo status do pedido.
     * @throws PedidoNaoEncontradoException Caso o pedido não exista.
     */
    @Override
    @Transactional
    public void atualizaStatusPedido(Long id, PedidoStatus pedidoStatus) {
        pedidoRepository.findById(id).map(p->{
            p.setPedidoStatus(pedidoStatus);
            return pedidoRepository.save(p);
        }).orElseThrow(PedidoNaoEncontradoException::new);
    }

    public Set<PedidoItens> convertItems(Pedido pedido, Set<PedidoItensDTO> itens){
        if(itens.isEmpty()){
            throw new RegraNegocioException("Itens não encontrado");
        }
        return itens.stream().map(dto->{
            Long idProduto = dto.getProdutoId();
            Produto produto = produtoRepository.findById(idProduto)
                    .orElseThrow(() -> new RegraNegocioException("Produto invalido"));
            PedidoItens pedidoItens = new PedidoItens();
            pedidoItens.setQuantidade(dto.getQuantidade());
            pedidoItens.setPedido(pedido);
            pedidoItens.setProduto(produto);
            return pedidoItens;
        }).collect(Collectors.toSet());
    }

    /**
     * Gera um relatório geral de pedidos, com a possibilidade de filtrar os pedidos por cliente.
     * O método é assíncrono e utiliza paralelismo condicional para melhorar o desempenho, especialmente quando
     * há uma grande quantidade de pedidos a serem processados. Quando um cliente é fornecido, o relatório
     * será restrito aos pedidos desse cliente. Caso contrário, o relatório incluirá todos os pedidos no sistema.
     * @param clienteId ID do cliente cujos pedidos devem ser retornados. Se for {@code null}, retorna todos os pedidos.
     * @return Um {@code CompletableFuture} contendo o conjunto de {@code PedidoDTO}s.
     * @throws IllegalArgumentException Se o {@code clienteId} for fornecido, mas o cliente não for encontrado.
     */
    @Override
    @Async
    public CompletableFuture<Set<PedidoDTO>> relatorioGeral(Long clienteId) {
        if (clienteId != null) {
            return buscarPedidosPorCliente(clienteId);
        } else {
            return buscarPedidosGerais();
        }
    }

    private CompletableFuture<Set<PedidoDTO>> buscarPedidosPorCliente(Long clienteId) {
        return CompletableFuture.supplyAsync(() -> {
            Cliente cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

            Set<Pedido> pedidos = pedidoRepository.findByClienteWithItems(cliente);
            return processarPedidosEmParalelo(pedidos);
        }, taskExecutor);
    }

    private CompletableFuture<Set<PedidoDTO>> buscarPedidosGerais() {
        return CompletableFuture.supplyAsync(() -> {
            Set<Pedido> pedidos = new HashSet<>(pedidoRepository.findAll());
            return processarPedidosEmParalelo(pedidos);
        }, taskExecutor);
    }

    private Set<PedidoDTO> processarPedidosEmParalelo(Set<Pedido> pedidos) {
        // Se o número de pedidos for grande, divide em partes menores para processar em paralelo
        if (pedidos.size() > 100) {
            Set<Pedido> pedidosSet = new HashSet<>(pedidos);  // Garantir que seja um Set
            Set<CompletableFuture<Set<PedidoDTO>>> futures = new HashSet<>();

            // Divide os pedidos em partes menores e processa em paralelo
            Iterator<Pedido> iterator = pedidosSet.iterator();
            while (iterator.hasNext()) {
                Set<Pedido> subSet = new HashSet<>();
                for (int i = 0; i < 20 && iterator.hasNext(); i++) {
                    subSet.add(iterator.next());
                }

                futures.add(CompletableFuture.supplyAsync(() ->
                        subSet.stream().map(PedidoDTO::new).collect(Collectors.toSet()), taskExecutor));
            }

            // Combina os resultados
            return futures.stream()
                    .map(CompletableFuture::join)
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
        } else {
            return pedidos.stream()
                    .map(PedidoDTO::new)
                    .collect(Collectors.toSet());
        }
    }



}
