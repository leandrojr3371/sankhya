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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;

    private final ClienteRepository clienteRepository;

    private final ProdutoRepository produtoRepository;

    private final PedidoItensRepository pedidoItensRepository;

    private final Executor taskExecutor;

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

    @Override
    @Async
    public CompletableFuture<Set<PedidoDTO>> relatorioGeral(Long clienteId, boolean carregarItens) {
        if (clienteId != null) {
            // Se o clienteId for fornecido, consulta apenas os pedidos desse cliente
            Cliente cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

            return CompletableFuture.supplyAsync(() -> {
                Set<Pedido> pedidos;

                if (carregarItens) {
                    // Consulta os pedidos do cliente com os itens carregados
                    pedidos = pedidoRepository.findByClienteWithItems(cliente);
                } else {
                    // Consulta os pedidos sem os itens
                    pedidos = pedidoRepository.findByCliente(cliente);
                }

                // Agora retorna diretamente um Set de PedidoDTO
                return pedidos.stream()
                        .map(pedido -> new PedidoDTO(pedido, carregarItens)) // Aqui estamos convertendo diretamente para o DTO
                        .collect(Collectors.toSet());
            }, taskExecutor);
        } else {
            // Se o clienteId não for fornecido, consulta todos os pedidos de todos os clientes
            return CompletableFuture.supplyAsync(() -> {
                Set<Pedido> pedidos;

                if (carregarItens) {
                    // Consulta todos os pedidos com os itens
                    pedidos = pedidoRepository.findAllWithItems();
                } else {
                    // Consulta todos os pedidos sem os itens
                    pedidos = pedidoRepository.findAll().stream().collect(Collectors.toSet());  // Mesmo aqui, podemos converter diretamente para DTO
                }

                // Agora retorna diretamente um Set de PedidoDTO
                return pedidos.stream()
                        .map(pedido -> new PedidoDTO(pedido, carregarItens))  // Aqui também, estamos convertendo para o DTO
                        .collect(Collectors.toSet());
            }, taskExecutor);
        }
    }


}
