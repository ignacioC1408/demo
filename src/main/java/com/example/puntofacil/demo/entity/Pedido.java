package com.example.puntofacil.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigoRetiro;
    private String qrCodeUrl;
    private LocalDateTime fecha;
    private String estado; // PENDIENTE, PAGADO, PREPARANDO, etc.
    private BigDecimal total;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<Pago> pagos;

    public Pedido() {
    }

    public Pedido(Long id, String codigoRetiro, String qrCodeUrl, LocalDateTime fecha, String estado, BigDecimal total,
            List<Pago> pagos) {
        this.id = id;
        this.codigoRetiro = codigoRetiro;
        this.qrCodeUrl = qrCodeUrl;
        this.fecha = fecha;
        this.estado = estado;
        this.total = total;
        this.pagos = pagos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoRetiro() {
        return codigoRetiro;
    }

    public void setCodigoRetiro(String codigoRetiro) {
        this.codigoRetiro = codigoRetiro;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    
}
