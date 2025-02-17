package com.example.sankhya.controller;

import com.example.sankhya.controller.dto.*;
import com.example.sankhya.domain.Pedido;
import com.example.sankhya.domain.PedidoItens;
import com.example.sankhya.domain.enums.PedidoStatus;
import com.example.sankhya.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pedido")
@Tag(name = "Pedido", description = "Operações relacionadas aos pedidos.")
public class PedidoController {

    private PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService){

        this.pedidoService = pedidoService;
    }

    @Operation(summary = "Salvar um novo Pedido", description = "Cria um novo pedido na base de dados.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso")
            })
    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public Long save(@RequestBody PedidoDTO requestDTO){
        Pedido pedido = pedidoService.save(requestDTO);
        return pedido.getId();
    }

    @Operation(summary = "Buscar Pedido por ID", description = "Retorna os detalhes de um pedido baseado no ID fornecido.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
                    @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
            })
    @GetMapping("{id}")
    public InformacoesPedidoDTO getById(@PathVariable Long id){
        return pedidoService.informacoesPedido(id)
                .map(this::informacoesPedidoDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }

    private InformacoesPedidoDTO informacoesPedidoDTO(Pedido pedido){
        return InformacoesPedidoDTO.builder()
                .codigo(pedido.getId())
                .dataPedido(pedido.getDataPedido())
                .cpf(pedido.getCliente().getCpf())
                .nomeCliente(pedido.getCliente().getNome())
                .total(pedido.getTotal())
                .pedidoItens(convertePedidoItens(pedido.getPedidoItens()))
                .status(pedido.getPedidoStatus().name())
                .build();
    }

    private Set<InformacoesPedidoItensDTO> convertePedidoItens(Set<PedidoItens> itens){
        if(itens.isEmpty()){
            return Collections.emptySet();
        }

        return itens.stream().map(item->
                InformacoesPedidoItensDTO.builder()
                        .descricao(item.getProduto().getDescricao())
                        .precoUnitario(item.getProduto().getPrecoUnitario())
                        .quantidade(item.getQuantidade())
                        .build()
        ).collect(Collectors.toSet());
    }

    @Operation(summary = "Cancelar Pedido", description = "Atualiza o status do pedido para 'Cancelado'.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Pedido cancelado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
            })
    @PatchMapping("/cancelaPedido/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelaPedido(@RequestBody AtualizaStatusPedido status, @PathVariable Long id){
        pedidoService.atualizaStatusPedido(id, PedidoStatus.valueOf(status.getNovoStatus()));
    }

    @Operation(summary = "Relatório Geral de Pedidos", description = "Retorna um relatório geral dos pedidos, com opção de carregar itens detalhados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Relatório de pedidos"),
            })
    @PreAuthorize("hasRole('CHEFE')")
    @GetMapping("/relatorio")
    public CompletableFuture<Set<PedidoDTO>> getRelatorioGeral(PedidoFiltroDTO filtro) {
        return pedidoService.relatorioGeral(filtro);
    }

}
