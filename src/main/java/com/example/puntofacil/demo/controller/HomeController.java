package com.example.puntofacil.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.puntofacil.demo.service.ProductoService;

@Controller
public class HomeController {

    private final ProductoService productoService;

    public HomeController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping({"/", "/index", "/home"})
    public String home(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        return "index";
    }
}
