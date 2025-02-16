package com.example.sankhya.controller.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InformacoesPedidoDTO {
    private Long codigo;
    private String cpf;
    private String nomeCliente;
    private BigDecimal total;
    private Set<InformacoesPedidoItensDTO> pedidoItens;
    private LocalDate dataPedido;
    private String status;
}
