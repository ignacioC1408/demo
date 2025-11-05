package com.example.puntofacil.demo.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.puntofacil.demo.entity.Empleado;
import com.example.puntofacil.demo.repository.EmpleadoRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/empleado")
public class EmpleadoLoginController {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoLoginController(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        return "empleado-login";
    }

    @PostMapping("/login")
public String procesarLogin(@RequestParam String username,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {

    Optional<Empleado> optEmpleado = empleadoRepository.findByUsername(username);

    if (optEmpleado.isEmpty()) {
        model.addAttribute("error", "Usuario no encontrado");
        return "empleado-login";
    }

    Empleado empleado = optEmpleado.get();

    if (!empleado.getPassword().equals(password)) {
        model.addAttribute("error", "Contraseña incorrecta");
        return "empleado-login";
    }

    session.setAttribute("empleado", empleado);

    // Redirige según el rol (usa los valores que tengas en tu BD)
    switch (empleado.getRol()) {
        case DUENIO:
            return "redirect:/empleado/panel/admin";
        case CAJERA:
            return "redirect:/empleado/panel/cajero"; // Cambié a cajero
        case VENDEDOR:
            return "redirect:/empleado/panel/vendedor";
        default:
            model.addAttribute("error", "Rol desconocido");
            return "empleado-login";
    }
}
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/empleado/login";
    }
}