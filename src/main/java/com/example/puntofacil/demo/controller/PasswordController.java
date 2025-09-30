package com.example.puntofacil.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.puntofacil.demo.entity.Empleado;
import com.example.puntofacil.demo.repository.EmpleadoRepository;

@Controller
public class PasswordController {

    private final EmpleadoRepository empleadoRepository;

    public PasswordController(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @GetMapping("/cambiar-password")
    public String mostrarFormulario() {
        return "cambiar_password";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam String actual,
                                  @RequestParam String nueva,
                                  @RequestParam String username,
                                  Model model) {
        Empleado empleado = empleadoRepository.findByUsername(username).orElse(null);

        if (empleado != null && empleado.getPassword().equals(actual)) {
            empleado.setPassword(nueva);
            empleadoRepository.save(empleado);
            model.addAttribute("mensaje", "Contraseña actualizada correctamente.");
        } else {
            model.addAttribute("mensaje", "La contraseña actual no es correcta.");
        }

        return "cambiar_password";
    }
}
