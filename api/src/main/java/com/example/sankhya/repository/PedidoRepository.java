package com.example.sankhya.repository;

import com.example.sankhya.domain.Cliente;
import com.example.sankhya.domain.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Set<Pedido> findByCliente(Cliente client);

    Optional<Pedido> findById(Long id);

    @Query("SELECT p FROM Pedido p JOIN FETCH p.pedidoItens WHERE p.cliente = :client")
    Set<Pedido> findByClienteWithItems(@Param("client") Cliente client);

    @Query("SELECT p FROM Pedido p JOIN FETCH p.pedidoItens")
    Set<Pedido> findAllWithItems();

}
