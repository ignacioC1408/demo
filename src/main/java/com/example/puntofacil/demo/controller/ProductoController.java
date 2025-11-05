package com.example.puntofacil.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.puntofacil.demo.entity.Producto;
import com.example.puntofacil.demo.entity.UnidadMedida;
import com.example.puntofacil.demo.repository.CategoriaRepository;
import com.example.puntofacil.demo.repository.ProductoRepository;

@Controller
@RequestMapping("/usuario/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public String listarProductos(
            @RequestParam(required = false) Long categoria,
            @RequestParam(required = false) String buscar,
            Model model) {

        List<Producto> productos;

        if (categoria != null && categoria > 0) {
            productos = productoRepository.findByCategoriaIdOrderByNombreAsc(categoria);
        } else if (buscar != null && !buscar.isEmpty()) {
            productos = productoRepository.findByNombreContainingIgnoreCaseOrderByNombreAsc(buscar);
        } else {
            productos = productoRepository.findAllByOrderByNombreAsc();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("categoriaSeleccionada", categoria);
        model.addAttribute("busqueda", buscar);
        return "usuario-productos";
    }

    // Endpoint para devolver el step por unidad (usado en carrito)
    @GetMapping("/{id}/step")
    @ResponseBody
    public String getStep(@PathVariable Long id) {
    return productoRepository.findById(id)
        .map(Producto::getUnidadMedida)
        .map(um -> {
            if (um == UnidadMedida.KG || um == UnidadMedida.LT) {
                return "0.1";
            }
            if (um == UnidadMedida.UNIDAD) {
                return "1";
            }
            return "1";
        })
        .orElse("1");
    }    
}