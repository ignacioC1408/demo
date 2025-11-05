package com.example.puntofacil.demo.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.example.puntofacil.demo.entity.Usuario;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute
    public void agregarUsuarioAlModelo(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
        }
    }
}

