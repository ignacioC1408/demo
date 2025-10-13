package com.example.puntofacil.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario/pago")
public class PagoController {

    @PostMapping("/procesar")
    public String procesarPago(HttpSession session) {
        System.out.println("✅ PagoController: Forward a procesar-pago");
        // Usa forward para mantener el método POST
        return "forward:/usuario/carrito/procesar-pago";
    }

    @GetMapping("/exitoso")
    public String pagoExitoso() {
        return "pago-exitoso";
    }

    @GetMapping("/fallido")
    public String pagoFallido() {
        return "pago-fallido";
    }

    @GetMapping("/pendiente")
    public String pagoPendiente() {
        return "pago-pendiente";
    }
}