package com.example.puntofacil.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.puntofacil.demo.entity.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    Optional<Empleado> findByUsername(String username);

    Optional<Empleado> findByTokenRecuperacion(String token); // 👈 nuevo método
}
