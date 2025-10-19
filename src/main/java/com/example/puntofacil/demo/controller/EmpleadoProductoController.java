package com.example.puntofacil.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.puntofacil.demo.entity.Producto;
import com.example.puntofacil.demo.repository.ProductoRepository;

@Controller
@RequestMapping("/empleado/productos")
public class EmpleadoProductoController {

    private final ProductoRepository productoRepository;

    public EmpleadoProductoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // LISTAR PRODUCTOS
    @GetMapping
    public String listarProductos(Model model) {
        List<Producto> productos = productoRepository.findAll();
        model.addAttribute("productos", productos);
        return "producto-list"; // ← CAMBIADO: sin "empleado/"
    }

    // FORMULARIO NUEVO PRODUCTO
    @GetMapping("/nuevo")
    public String nuevoProductoForm(Model model) {
        model.addAttribute("producto", new Producto());
        return "producto-form"; // ← CAMBIADO: sin "empleado/"
    }

    // FORMULARIO EDITAR PRODUCTO
    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable Long id, Model model) {
        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isPresent()) {
            model.addAttribute("producto", producto.get());
            return "producto-form"; // ← CAMBIADO: sin "empleado/"
        }
        return "redirect:/empleado/productos";
    }

    // GUARDAR PRODUCTO
    @PostMapping("/guardar")
    public String guardarProducto(Producto producto) {
        productoRepository.save(producto);
        return "redirect:/empleado/productos";
    }

    // ELIMINAR PRODUCTO
    @PostMapping("/borrar/{id}")
    public String borrarProducto(@PathVariable Long id) {
        productoRepository.deleteById(id);
        return "redirect:/empleado/productos";
    }

    // Endpoint para step
    @GetMapping("/{id}/step")
    @ResponseBody
    public String getStep(@PathVariable Long id) {
        Optional<Producto> opt = productoRepository.findById(id);
        if (opt.isPresent()) {
            Producto p = opt.get();
            String um = p.getUnidadMedida();
            if ("KG".equalsIgnoreCase(um)) return "0.1";
            if ("UNIDAD".equalsIgnoreCase(um)) return "1";
            if ("LT".equalsIgnoreCase(um)) return "0.1";
            return "1";
        }
        return "1";
    }
}