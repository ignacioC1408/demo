package com.example.puntofacil.demo.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.puntofacil.demo.entity.Empleado;
import com.example.puntofacil.demo.entity.EstadoPedido;
import com.example.puntofacil.demo.entity.EstadoRol;
import com.example.puntofacil.demo.entity.Pedido;
import com.example.puntofacil.demo.repository.EmpleadoRepository;
import com.example.puntofacil.demo.repository.PedidoRepository;
import com.example.puntofacil.demo.repository.ProductoRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/empleado/panel")
public class EmpleadoPanelController {

    private final SimpMessagingTemplate messagingTemplate;
    private final PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    public EmpleadoPanelController(PedidoRepository pedidoRepository,
                                   SimpMessagingTemplate messagingTemplate) {
        this.pedidoRepository = pedidoRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // =====================================
    // PANELES PRINCIPALES (admin, cajero, vendedor)
    // =====================================

    @GetMapping("/inicio")
    public String panelInicio(HttpSession session, RedirectAttributes redirectAttributes) {
        Empleado empleado = (Empleado) session.getAttribute("empleado");
        if (empleado == null) return "redirect:/empleado/login";

        return switch (empleado.getRol()) {
            case DUENIO ->"redirect:/empleado/panel/admin";
            case CAJERA ->"redirect:/empleado/panel/cajero";
            case VENDEDOR ->"redirect:/empleado/panel/vendedor";
            default -> { 
            redirectAttributes.addFlashAttribute("error", "Rol no válido");
            yield "redirect:/empleado/login";
            }
        };
    }

    @GetMapping("/admin")
    public String panelAdmin(HttpSession session, Model model) {
        if (!addGlobalAttributes(session, model)) return "redirect:/empleado/login";
        if (!validarRol(session, List.of(EstadoRol.DUENIO))) return "redirect:/empleado/login?error=acceso_denegado";
        agregarEstadisticasAdmin(model);
        return "panel-admin";
    }

    @GetMapping("/cajero")
    public String panelCajero(HttpSession session, Model model) {
        if (!addGlobalAttributes(session, model)) return "redirect:/empleado/login";
        if (!validarRol(session, List.of(EstadoRol.CAJERA, EstadoRol.DUENIO))) return "redirect:/empleado/login?error=acceso_denegado";
        agregarEstadisticasCajero(model);
        return "panel-cajero";
    }

    @GetMapping("/vendedor")
    public String panelVendedor(HttpSession session, Model model) {
        if (!addGlobalAttributes(session, model)) return "redirect:/empleado/login";
        if (!validarRol(session, List.of(EstadoRol.VENDEDOR, EstadoRol.DUENIO))) return "redirect:/empleado/login?error=acceso_denegado";
        agregarEstadisticasVendedor(model);
        return "panel-vendedor";
    }

    // =====================================
    // GESTIÓN DE PEDIDOS
    // =====================================

    @GetMapping("/pedidos")
    public String listarPedidos(@RequestParam(value="estado", required=false) EstadoPedido estado,
                                HttpSession session, Model model) {
        if (!addGlobalAttributes(session, model)) return "redirect:/empleado/login";

        List<Pedido> pedidos = (estado != null)
                ? pedidoRepository.findByEstado(estado)
                : pedidoRepository.findAll();

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("estadoSeleccionado", estado);
        return "empleado-pedido"; // vista de lista de pedidos
    }

    @GetMapping("/pedido/{id}")
    public String verPedido(@PathVariable Long id, HttpSession session, Model model) {
        if (!addGlobalAttributes(session, model)) return "redirect:/empleado/login";

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        model.addAttribute("pedido", pedido);
        return "pedido-detalle"; // vista detalle del pedido
    }

    @PostMapping("/pedido/{id}/estado")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam EstadoPedido estado,
                                HttpSession session) {
        if (session.getAttribute("empleado") == null) return "redirect:/empleado/login";

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        pedido.setEstado(estado);
        pedidoRepository.save(pedido);

        // 🔔 Notificar por WebSocket a todos
        messagingTemplate.convertAndSend("/topic/todos", pedido);

        return "redirect:/empleado/panel/pedidos";
    }

    // =====================================
    // UTILIDADES / ESTADÍSTICAS
    // =====================================

    private boolean addGlobalAttributes(HttpSession session, Model model) {
        Empleado empleado = (Empleado) session.getAttribute("empleado");
        if (empleado == null) return false;
        model.addAttribute("username", empleado.getUsername());
        model.addAttribute("rol", empleado.getRol());
        return true;
    }

    private boolean validarRol(HttpSession session, List<EstadoRol> rolesPermitidos) {
        Empleado empleado = (Empleado) session.getAttribute("empleado");
        return empleado != null 
            && empleado.getRol() != null
            && rolesPermitidos.contains(empleado.getRol());
    }

    private void agregarEstadisticasAdmin(Model model) {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.atTime(23, 59, 59);

        BigDecimal ventasHoy = pedidoRepository.sumTotalByFechaBetween(inicio, fin);
        model.addAttribute("ventasHoy", ventasHoy != null ? ventasHoy : BigDecimal.ZERO);

        YearMonth mesActual = YearMonth.now();
        LocalDate inicioMes = mesActual.atDay(1);
        LocalDate finMes = mesActual.atEndOfMonth();
        BigDecimal ingresosMensuales = pedidoRepository.sumTotalByFechaBetween(inicioMes.atStartOfDay(), finMes.atTime(23, 59, 59));
        model.addAttribute("ingresosMensuales", ingresosMensuales != null ? ingresosMensuales : BigDecimal.ZERO);
        model.addAttribute("productosStock", productoRepository.count());
    }

    private void agregarEstadisticasCajero(Model model) {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.atTime(23, 59, 59);
        BigDecimal ventasHoy = pedidoRepository.sumTotalByFechaBetween(inicio, fin);
        model.addAttribute("ventasHoy", ventasHoy != null ? ventasHoy : BigDecimal.ZERO);
    }

    private void agregarEstadisticasVendedor(Model model) {
        model.addAttribute("esVendedor", true);
    }
}
