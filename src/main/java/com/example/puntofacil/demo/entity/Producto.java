
package com.example.puntofacil.demo.entity;

import java.math.BigDecimal;

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
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_producto", nullable = false, unique = true)
    private Integer codigoProducto;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "codigo_barra", unique = true)
    private String codigoBarra;

    @Column(name = "precio_base", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioBase;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_medida", length = 10)
    private UnidadMedida unidadMedida;
    
    @Column(name = "stock" ,precision = 10, scale = 3, nullable = false)
    private BigDecimal stock;

    @ManyToOne 
    @JoinColumn(name = "categoria_id") 
    private Categoria categoria; 

    
    public Producto() {}

    public Producto(Integer codigoProducto, String nombre, String codigoBarra,
                    BigDecimal precioBase, UnidadMedida unidadMedida, BigDecimal stock, Categoria categoria) {
        this.codigoProducto = codigoProducto;
        this.nombre = nombre;
        this.codigoBarra = codigoBarra;
        this.precioBase = precioBase;
        this.unidadMedida = unidadMedida;
        this.stock = stock;
        this.categoria = categoria; 
    }

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getCodigoProducto() { return codigoProducto; }
    public void setCodigoProducto(Integer codigoProducto) { this.codigoProducto = codigoProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigoBarra() { return codigoBarra; }
    public void setCodigoBarra(String codigoBarra) { this.codigoBarra = codigoBarra; }

    public BigDecimal getPrecioBase() { return precioBase; }
    public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }

    public UnidadMedida getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(UnidadMedida unidadMedida) { this.unidadMedida = unidadMedida; }

    public BigDecimal getStock() { return stock; }
    public void setStock(BigDecimal stock) { this.stock = stock; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}