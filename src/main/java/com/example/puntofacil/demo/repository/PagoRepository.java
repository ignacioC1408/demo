package com.example.puntofacil.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.puntofacil.demo.entity.EstadoPago;
import com.example.puntofacil.demo.entity.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    
    // Buscar pagos por usuario, ordenados por fecha más reciente
    List<Pago> findByUsuario_IdOrderByFechaDesc(Long usuarioId);
    
    // Buscar pago por paymentId (ID de transacción de Mercado Pago)
    Optional<Pago> findByMpPaymentId(String mpPaymentId);
    
    // Buscar pagos por estado
    List<Pago> findByEstado(EstadoPago estado);
    
    // Buscar pagos pendientes de un usuario
    @Query("SELECT p FROM Pago p WHERE p.usuario.id = :usuarioId AND p.estado = 'PENDIENTE'")
    List<Pago> findPagosPendientesByUsuarioId(@Param("usuarioId") Long usuarioId);
}