package com.example.puntofacil.demo.controller;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.puntofacil.demo.entity.Empleado;
import com.example.puntofacil.demo.entity.Pedido;
import com.example.puntofacil.demo.service.PedidoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/empleado/panel")
public class EmpleadoVentaController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/ventas")
    public String ventas(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                         HttpSession session,
                         Model model) {

        // Validar sesión
        Empleado empleado = (Empleado) session.getAttribute("empleado");
        if (empleado == null) return "redirect:/empleado/login";
        model.addAttribute("username", empleado.getUsername());
        model.addAttribute("rol", empleado.getRol().toUpperCase());

        // Fechas por defecto = hoy
        if (desde == null) desde = LocalDate.now();
        if (hasta == null) hasta = LocalDate.now();
        
        // Datos de ventas
        List<Pedido> ventas = pedidoService.listarVentasPorRango(desde, hasta);
        BigDecimal totalRango = pedidoService.totalVentasPorRango(desde, hasta);
        BigDecimal ventasHoy = pedidoService.ventasDeHoy();

        model.addAttribute("pedidos", ventas);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("totalVentas", totalRango != null ? totalRango : BigDecimal.ZERO);
        model.addAttribute("ventasHoy", ventasHoy != null ? ventasHoy : BigDecimal.ZERO);
        model.addAttribute("hoy", LocalDate.now());
        model.addAttribute("lunesSemana", LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
        model.addAttribute("primerDiaMes", LocalDate.now().withDayOfMonth(1));
        return "empleado-ventas"; // 👉 ver vista abajo
    }
}

