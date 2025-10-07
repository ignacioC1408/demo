package com.example.puntofacil.demo.service;

import org.springframework.stereotype.Service;

import com.example.puntofacil.demo.entity.EstadoPago;
import com.example.puntofacil.demo.entity.EstadoPedido;
import com.example.puntofacil.demo.entity.MetodoPago;
import com.example.puntofacil.demo.entity.Pago;
import com.example.puntofacil.demo.entity.Pedido; 
import com.example.puntofacil.demo.repository.PagoRepository;
import com.example.puntofacil.demo.repository.PedidoRepository;

import java.math.BigDecimal;

@Service
public class PagoService {
    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;

    public PagoService(PagoRepository pagoRepository, PedidoRepository pedidoRepository) {
        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
    }
    
    /** 
     * @param pedidoId ID del pedido
     * @param metodo Método de pago (MERCADOPAGO, EFECTIVO, TRANSFERENCIA)
     * @param mpPaymentId Identificador externo de Mercado Pago
     * @param estadoMp Estado recibido desde MP (APPROVED, REJECTED, etc.)
     * @param monto Monto pagado
     * @return Pago registrado
     */

    public void registrarPago(Long pedidoId, MetodoPago metodo, String mpPaymentId, String estadoMp, BigDecimal monto) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow();

        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMetodo(metodo);
        pago.setMpPaymentId(mpPaymentId);
        pago.setMonto(monto);

        // Mapear estados de MP a internos
        switch (estadoMp.toUpperCase()) {
            case "APPROVED" -> pago.setEstado(EstadoPago.RECIBIDO);
            case "REJECTED" -> pago.setEstado(EstadoPago.RECHAZADO);
            default -> pago.setEstado(EstadoPago.PENDIENTE);
        }

        pagoRepository.save(pago);

        // Si el pago fue aprobado, actualizamos el pedido
        if (pago.getEstado() == EstadoPago.RECIBIDO) {
            pedido.setEstado (EstadoPedido.PAGADO);
            pedidoRepository.save(pedido);
        }
    }
}
