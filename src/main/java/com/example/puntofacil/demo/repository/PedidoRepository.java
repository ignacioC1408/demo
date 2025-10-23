package com.example.puntofacil.demo.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.puntofacil.demo.entity.EstadoPedido;
import com.example.puntofacil.demo.entity.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<Pedido> findByCodigoRetiro(String codigoRetiro);

    List<Pedido> findByEstado(EstadoPedido estado);

    List<Pedido> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    // ✅ NUEVO: ventas = pedidos con estado PAGADO en rango
    List<Pedido> findByEstadoAndFechaBetween(EstadoPedido estado, LocalDateTime inicio, LocalDateTime fin);

    @Query("""
                SELECT COALESCE(SUM(p.total), 0)
                FROM Pedido p
                WHERE p.fecha BETWEEN :inicio AND :fin
            """)
    BigDecimal sumTotalByFechaBetween(@Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

     // ✅ NUEVO: total por estado en rango (ventas)
    @Query("""
           SELECT COALESCE(SUM(p.total), 0)
           FROM Pedido p
           WHERE p.estado = :estado
             AND p.fecha BETWEEN :inicio AND :fin
           """)
    BigDecimal sumTotalByEstadoAndFechaBetween(@Param("estado") EstadoPedido estado,
                                               @Param("inicio") LocalDateTime inicio,
                                               @Param("fin") LocalDateTime fin);    
}
