package com.example.puntofacil.demo.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import com.example.puntofacil.demo.entity.Empleado;
import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalControllerAdvice {
    
    @ModelAttribute
    public void agregarUsuarioGlobal(Model model, HttpSession session) {
        Empleado empleado = (Empleado) session.getAttribute("empleado");
        
        if (empleado != null) {
            model.addAttribute("username", empleado.getUsername());
            model.addAttribute("rol", empleado.getRol());
            model.addAttribute("nombreUsuario", empleado.getUsername());
            model.addAttribute("rolUsuario", empleado.getRol());
        } else {
            model.addAttribute("username", "Invitado");
            model.addAttribute("rol", "INVITADO");
        }
    }
}