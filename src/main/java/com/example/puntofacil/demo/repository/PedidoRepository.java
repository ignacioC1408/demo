package com.example.puntofacil.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.puntofacil.demo.entity.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido,Long> {

}
