package com.example.puntofacil.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(name = "monto", precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo")
    private EstadoMetodo metodo;

    @Enumerated(EnumType.STRING)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @Column(name = "mp_payment_id", length = 100)
    private String mpPaymentId;

    @Column(name = "fecha")
    private LocalDateTime fecha;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;



    // Constructor vacío requerido por JPA
    public Pago() {}

    // Constructor para crear nuevo pago
    public Pago(Long id, Pedido pedido, BigDecimal monto, EstadoMetodo metodo, EstadoPago estado,
             String mpPaymentId, LocalDateTime fecha) {
        this.id = id;
        this.pedido = pedido;
        this.monto = monto;
        this.metodo = metodo;
        this.estado = estado;
        this.mpPaymentId = mpPaymentId;
        this.fecha = fecha;
    }
    // ========== GETTERS Y SETTERS ==========
    
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

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public EstadoMetodo getMetodo() {
        return metodo;
    }

    public void setMetodo(EstadoMetodo metodo) {
        this.metodo = metodo;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}