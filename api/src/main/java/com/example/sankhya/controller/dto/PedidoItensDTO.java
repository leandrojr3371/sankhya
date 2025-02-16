package com.example.sankhya.controller.dto;

import com.example.sankhya.domain.PedidoItens;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PedidoItensDTO {
    private Long produtoId;
    private Integer quantidade;
    private String produto;
    private BigDecimal preco;
    private BigDecimal totalItem;

    public PedidoItensDTO(PedidoItens pedidoItens) {
        this.produtoId = pedidoItens.getProduto().getId();
        this.quantidade = pedidoItens.getQuantidade();
        this.produto = pedidoItens.getProduto().getDescricao();
        this.preco = pedidoItens.getProduto().getPrecoUnitario();
        this.totalItem = this.preco.multiply(new BigDecimal(this.quantidade));
    }
}
