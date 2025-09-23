package com.example.puntofacil.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.puntofacil.demo.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsByCodigoProducto(Integer codigoProducto);
    Optional<Producto> findByCodigoProducto(Integer codigoProducto);
}