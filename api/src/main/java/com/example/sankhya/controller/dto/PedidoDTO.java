package com.example.sankhya.controller.dto;

import com.example.sankhya.domain.Pedido;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PedidoDTO {
    private Long clienteId;
    private BigDecimal total;
    private Set<PedidoItensDTO> pedidoItens;
    private String pedidoStatus;
    private String nomeCliente;
    private Long id;

    public PedidoDTO(Pedido pedido, boolean carregarItens) {
        this.clienteId = pedido.getCliente() != null ? pedido.getCliente().getId() : null;
        this.total = pedido.getTotal();
        this.pedidoItens = pedido.getPedidoItens().stream()
                .map(PedidoItensDTO::new)  // Convertendo cada PedidoItem para PedidoItensDTO
                .collect(Collectors.toSet());
        this.pedidoStatus = pedido.getPedidoStatus().name();
        this.nomeCliente = pedido.getCliente().getNome();
        this.id = pedido.getId();

        if (carregarItens) {
            this.pedidoItens = pedido.getPedidoItens().stream()
                    .map(PedidoItensDTO::new)
                    .collect(Collectors.toSet());
        }
    }
}
