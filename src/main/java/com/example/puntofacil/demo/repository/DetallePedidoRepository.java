package com.example.puntofacil.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.puntofacil.demo.entity.DetallePedido;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
}
