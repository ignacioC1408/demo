package com.example.puntofacil.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Column(name = "mp_payment_id")
    private String mpPaymentId;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodo; // MERCADOPAGO, EFECTIVO, TRANSFERENCIA

    @Enumerated(EnumType.STRING)
    private EstadoPago estado; // PENDIENTE, RECIBIDO, RECHAZADO

    private BigDecimal monto;
    private LocalDateTime fecha = LocalDateTime.now();
        
    public Pago() {
    }

    public Pago(Long id, Pedido pedido, String mpPaymentId, MetodoPago metodo, EstadoPago estado, BigDecimal monto,
                LocalDateTime fecha) {
        this.id = id;
        this.pedido = pedido;
        this.mpPaymentId = mpPaymentId;
        this.metodo = metodo;
        this.estado = estado;
        this.monto = monto;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Pedido getPedido() {
        return pedido;
    }
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
    public String getMpPaymentId() {
        return mpPaymentId;
    }
    public void setMpPaymentId(String mpPaymentId) {
        this.mpPaymentId = mpPaymentId;
    }
    public MetodoPago getMetodo() {
        return metodo;
    }
    public void setMetodo(MetodoPago metodo) {
        this.metodo = metodo;
    }
    public EstadoPago getEstado() {
        return estado;
    }
    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }
    public BigDecimal getMonto() {
        return monto;
    }
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
    public LocalDateTime getFecha() {
        return fecha;
    }
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    
}
