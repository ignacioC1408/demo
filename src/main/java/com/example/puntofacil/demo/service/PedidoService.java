package com.example.puntofacil.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.puntofacil.demo.entity.EstadoPedido;
import com.example.puntofacil.demo.entity.Pedido;
import com.example.puntofacil.demo.repository.PedidoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    private LocalDateTime startOfDay(LocalDate d) {
        return d.atStartOfDay();
    }

    private LocalDateTime endOfDay(LocalDate d) {
        return d.atTime(23, 59, 59, 999_000_000); // precision ~ microsegundos por tu schema (datetime(6))
    }

    // Lista de ventas (PAGADO) por rango de LocalDate (UI)
    public List<Pedido> listarVentasPorRango(LocalDate desde, LocalDate hasta) {
        LocalDateTime ini = startOfDay(desde);
        LocalDateTime fin = endOfDay(hasta);
        return pedidoRepository.findByEstadoAndFechaBetween(EstadoPedido.PAGADO, ini, fin);
    }

    // Total de ventas (PAGADO) por rango
    public BigDecimal totalVentasPorRango(LocalDate desde, LocalDate hasta) {
        LocalDateTime ini = startOfDay(desde);
        LocalDateTime fin = endOfDay(hasta);
        return pedidoRepository.sumTotalByEstadoAndFechaBetween(EstadoPedido.PAGADO, ini, fin);
    }

    // Ventas del día (PAGADO)
    public BigDecimal ventasDeHoy() {
        LocalDate hoy = LocalDate.now();
        return totalVentasPorRango(hoy, hoy);
    }
}

