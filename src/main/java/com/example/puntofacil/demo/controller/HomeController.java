package com.example.puntofacil.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("mensaje", "Bienvenido a Punto Fácil");
        return "home"; // se renderiza home.html
    }
}
