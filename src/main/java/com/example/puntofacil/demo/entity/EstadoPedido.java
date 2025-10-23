package com.example.puntofacil.demo.entity;

public enum EstadoPedido {
    PENDIENTE,     // Pedido creado por el cliente
    PESANDO,       // Vendedor pesa productos
    LISTO,         // Vendedor marca listo
    PAGADO,        // Cajera confirma el pago
    CANCELADO      // Pedido anulado
;

    public boolean isBlank() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isBlank'");
    }
}
