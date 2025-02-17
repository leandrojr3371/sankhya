package com.example.sankhya.service;

import com.example.sankhya.controller.dto.PedidoDTO;
import com.example.sankhya.domain.Pedido;
import com.example.sankhya.domain.enums.PedidoStatus;
import org.springframework.scheduling.annotation.Async;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface PedidoService {

    Pedido save(PedidoDTO pedidoDTO);

    Optional<Pedido> informacoesPedido(Long idPedido);

    void atualizaStatusPedido(Long id, PedidoStatus pedidoStatus);

    CompletableFuture<Set<PedidoDTO>> relatorioGeral(Long clienteId);
}
