package com.example.puntofacil.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.puntofacil.demo.entity.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    
}