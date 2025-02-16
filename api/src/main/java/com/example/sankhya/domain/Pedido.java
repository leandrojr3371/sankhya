package com.example.sankhya.domain;

import com.example.sankhya.domain.enums.PedidoStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Cliente cliente;

    private LocalDate dataPedido;

    private BigDecimal total;

    @OneToMany(mappedBy = "pedido", fetch = FetchType.EAGER)
    private Set<PedidoItens> pedidoItens;

    @Enumerated(EnumType.STRING)
    private PedidoStatus pedidoStatus;

}
