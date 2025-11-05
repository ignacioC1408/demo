package com.example.puntofacil.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.puntofacil.demo.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}
