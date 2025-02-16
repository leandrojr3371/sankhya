package com.example.sankhya.controller;

import com.example.sankhya.controller.dto.AtualizaStatusPedido;
import com.example.sankhya.controller.dto.InformacoesPedidoDTO;
import com.example.sankhya.controller.dto.InformacoesPedidoItensDTO;
import com.example.sankhya.controller.dto.PedidoDTO;
import com.example.sankhya.domain.Pedido;
import com.example.sankhya.domain.PedidoItens;
import com.example.sankhya.domain.enums.PedidoStatus;
import com.example.sankhya.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    private PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService){
        this.pedidoService = pedidoService;
    }

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public Long save(@RequestBody PedidoDTO requestDTO){
        Pedido pedido = pedidoService.save(requestDTO);
        return pedido.getId();
    }

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

    @PatchMapping("/updateStatus/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(@RequestBody AtualizaStatusPedido status, @PathVariable Long id){
        String newStatus = status.getNovoStatus();
        pedidoService.atualizaStatusPedido(id, PedidoStatus.valueOf(newStatus));
    }

    @GetMapping("/relatorio")
    public CompletableFuture<Set<PedidoDTO>> getRelatorioGeral(@RequestParam(required = false) Long clienteId,
                                                               @RequestParam(defaultValue = "false") boolean carregarItens) {
        return pedidoService.relatorioGeral(clienteId, carregarItens);
    }

}
