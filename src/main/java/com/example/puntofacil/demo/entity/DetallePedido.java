package com.example.puntofacil.demo.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cantidad_unidad", precision = 10, scale = 3, nullable = false)
    private BigDecimal cantidadUnidad;

    @Column(name = "precio_unitario", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // ===== CONSTRUCTORES =====
    public DetallePedido() {}

    public DetallePedido(BigDecimal cantidadUnidad, BigDecimal precioUnitario, Producto producto, Pedido pedido) {
        this.cantidadUnidad = cantidadUnidad;
        this.precioUnitario = precioUnitario;
        this.producto = producto;
        this.pedido = pedido;
    }

    // ===== GETTERS & SETTERS =====
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

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public BigDecimal getCantidadUnidad() {
        return cantidadUnidad;
    }

    public void setCantidadUnidad(BigDecimal cantidadUnidad) {
        this.cantidadUnidad = cantidadUnidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    // ===== MÉTODOS AUXILIARES =====
    public BigDecimal getSubtotal() {
        if (cantidadUnidad == null || precioUnitario == null) return BigDecimal.ZERO;
        return cantidadUnidad.multiply(precioUnitario);
    }
}
