package com.example.puntofacil.demo.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.puntofacil.demo.entity.Empleado;
import com.example.puntofacil.demo.entity.Pedido;
import com.example.puntofacil.demo.repository.PedidoRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/empleado/panel")
public class EmpleadoPanelController {

    private final PedidoRepository pedidoRepository;

    public EmpleadoPanelController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    // MÉTODO DE INICIO QUE REDIRIGE SEGÚN ROL
    @GetMapping("/inicio")
    public String panelInicio(HttpSession session) {
        Empleado empleado = (Empleado) session.getAttribute("empleado");
        
        if (empleado == null) {
            return "redirect:/empleado/login";
        }
        
        // Redirige según el rol (usa DUENIO o ADMIN según lo que tengas en BD)
        return switch(empleado.getRol().toUpperCase()) {
            case "ADMIN", "DUENIO" -> "redirect:/empleado/panel/admin";
            case "CAJERO", "CAJERA" -> "redirect:/empleado/panel/cajero";
            case "VENDEDOR" -> "redirect:/empleado/panel/vendedor";
            default -> "redirect:/empleado/login?error=rol_no_valido";
        };
    }

    // PANEL ADMIN - Solo para ADMIN/DUENIO
    @GetMapping("/admin")
    public String panelAdmin(HttpSession session, Model model) {
        if (!validarRol(session, List.of("ADMIN", "DUENIO"))) {
            return "redirect:/empleado/panel/inicio?error=acceso_denegado";
        }
        System.out.println("✅ DEBUG: Accediendo a panel admin");
        
        // Agregar estadísticas específicas para admin
        agregarEstadisticasAdmin(model);
        return "paneles/panel-admin";
    }

    // PANEL CAJERO - Solo para CAJERO/CAJERA y ADMIN
    @GetMapping("/cajero")
    public String panelCajero(HttpSession session, Model model) {
        if (!validarRol(session, List.of("CAJERO", "CAJERA", "ADMIN", "DUENIO"))) {
            return "redirect:/empleado/panel/inicio?error=acceso_denegado";
        }
        System.out.println("✅ DEBUG: Accediendo a panel cajero");
        
        // Agregar datos específicos para cajero
        agregarEstadisticasCajero(model);
        return "paneles/panel-cajero"; // Asegúrate de que esta vista existe
    }

    // PANEL VENDEDOR - Solo para VENDEDOR y ADMIN
    @GetMapping("/vendedor")
    public String panelVendedor(HttpSession session, Model model) {
        if (!validarRol(session, List.of("VENDEDOR", "ADMIN", "DUENIO"))) {
            return "redirect:/empleado/panel/inicio?error=acceso_denegado";
        }
        System.out.println("✅ DEBUG: Accediendo a panel vendedor");
        
        // Agregar datos específicos para vendedor
        agregarEstadisticasVendedor(model);
        return "paneles/panel-vendedor";
    }

    // MÉTODOS COMPARTIDOS CON VALIDACIÓN DE ROL
    @GetMapping("/ventas")
    public String panelVentas(HttpSession session) {
        Empleado empleado = (Empleado) session.getAttribute("empleado");
        return switch (empleado.getRol().toUpperCase()) {
            case "ADMIN", "DUENIO" -> "redirect:/empleado/panel/admin";
            case "CAJERO", "CAJERA" -> "redirect:/empleado/panel/cajero";
            case "VENDEDOR" -> "redirect:/empleado/panel/vendedor";
            default -> "redirect:/empleado/login?error=rol_no_valido";
        };
    }
    @GetMapping("/stock")
    public String panelStock(HttpSession session, Model model) {
        if (!validarRol(session, List.of("VENDEDOR", "ADMIN", "DUENIO"))) {
            return "redirect:/empleado/panel/inicio?error=acceso_denegado";
        }
        System.out.println("✅ Accediendo a stock");
        return "empleado-productos";
    }

    @GetMapping("/productos")
    public String panelProductos(HttpSession session, Model model) {
        if (!validarRol(session, List.of("ADMIN", "DUENIO"))) {
            return "redirect:/empleado/panel/inicio?error=acceso_denegado";
        }
        System.out.println("✅ Accediendo a productos");
        return "producto-list"; // Cambié a producto-list que es tu vista existente
    }

    // MÉTODOS EXISTENTES ACTUALIZADOS
    @GetMapping
    public String panelHome(HttpSession session, Model model) {
        Empleado empleado = (Empleado) session.getAttribute("empleado");
        if (empleado == null) {
            return "redirect:/empleado/login";
        }

        // Tu lógica existente de panelHome
        List<Pedido> todos = pedidoRepository.findAll();
        Map<String, Long> cuentasPorEstado = todos.stream()
                .collect(Collectors.groupingBy(p -> p.getEstado() == null ? "SIN_ESTADO" : p.getEstado(), Collectors.counting()));
        model.addAttribute("cuentasPorEstado", cuentasPorEstado);

        YearMonth ym = YearMonth.now();
        LocalDate desdeMes = ym.atDay(1);
        LocalDate hastaMes = ym.atEndOfMonth();

        LocalDate desdeAnio = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate hastaAnio = LocalDate.of(LocalDate.now().getYear(), 12, 31);

        BigDecimal totalMes = pedidoRepository.sumTotalByFechaBetween(desdeMes, hastaMes);
        BigDecimal totalAnio = pedidoRepository.sumTotalByFechaBetween(desdeAnio, hastaAnio);

        model.addAttribute("totalMes", totalMes == null ? BigDecimal.ZERO : totalMes);
        model.addAttribute("totalAnio", totalAnio == null ? BigDecimal.ZERO : totalAnio);
        model.addAttribute("ultimosPedidos", pedidoRepository.findByFechaBetween(desdeMes.minusDays(7), hastaMes));

        return "empleado/panel-home";
    }

    @GetMapping("/pedidos")
    public String listarPedidos(@RequestParam(value="estado", required=false) String estado, 
                               HttpSession session, Model model) {
        Empleado empleado = (Empleado) session.getAttribute("empleado");
        if (empleado == null) {
            return "redirect:/empleado/login";
        }

        List<Pedido> pedidos;
        if (estado != null && !estado.isBlank()) pedidos = pedidoRepository.findByEstado(estado);
        else pedidos = pedidoRepository.findAll();
        model.addAttribute("pedidos", pedidos);
        return "empleado/pedidos-list";
    }

    @GetMapping("/pedido/{id}")
    public String detallePedido(@PathVariable Long id, HttpSession session, Model model) {
        Empleado empleado = (Empleado) session.getAttribute("empleado");
        if (empleado == null) {
            return "redirect:/empleado/login";
        }

        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) return "redirect:/empleado/panel?errNotFound";
        model.addAttribute("pedido", pedido);
        return "empleado/pedido-detalle";
    }

    @PostMapping("/pedido/{id}/estado")
    public String actualizarEstado(@PathVariable Long id, @RequestParam String estado, HttpSession session) {
        Empleado empleado = (Empleado) session.getAttribute("empleado");
        if (empleado == null) {
            return "redirect:/empleado/login";
        }

        pedidoRepository.findById(id).ifPresent(p -> {
            p.setEstado(estado);
            pedidoRepository.save(p);
        });
        return "redirect:/empleado/panel/pedido/" + id;
    }

    // MÉTODOS PRIVADOS DE UTILIDAD
    private boolean validarRol(HttpSession session, List<String> rolesPermitidos) {
        Empleado empleado = (Empleado) session.getAttribute("empleado");
        if (empleado == null) return false;
        
        return rolesPermitidos.stream()
                .anyMatch(rol -> rol.equalsIgnoreCase(empleado.getRol()));
    }

    private void agregarEstadisticasAdmin(Model model) {
        // Estadísticas específicas para admin
        // Puedes agregar total de empleados, productos, etc.
        model.addAttribute("esAdmin", true);
    }

    private void agregarEstadisticasCajero(Model model) {
        // Estadísticas específicas para cajero
        LocalDate hoy = LocalDate.now();
        BigDecimal ventasHoy = pedidoRepository.sumTotalByFechaBetween(hoy, hoy);
        model.addAttribute("ventasHoy", ventasHoy != null ? ventasHoy : BigDecimal.ZERO);
    }

    private void agregarEstadisticasVendedor(Model model) {
        // Estadísticas específicas para vendedor
        model.addAttribute("esVendedor", true);
    }
}