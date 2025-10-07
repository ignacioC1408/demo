package com.example.puntofacil.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.puntofacil.demo.entity.CarritoItem;
import com.example.puntofacil.demo.entity.Producto;
import com.example.puntofacil.demo.entity.Usuario;
import com.example.puntofacil.demo.repository.ProductoRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CarritoController {

    private final ProductoRepository productoRepository;

    public CarritoController(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // 🔹 Ver carrito - CON CÁLCULO DEL TOTAL
    @GetMapping("/carrito/ver")
    public String verCarrito(HttpSession session, Model model, HttpServletRequest request) {
        System.out.println("🛒 DEBUG: ===== INICIO VER CARRITO =====");
        System.out.println("🔗 URL: " + request.getRequestURL());
        System.out.println("🆔 ID de Sesión: " + session.getId());
        
        // ✅ VERIFICACIÓN DETALLADA DE LA SESIÓN
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        System.out.println("👤 Usuario en sesión: " + (usuario != null ? usuario.getNombre() : "NULL"));
        
        // Listar TODOS los atributos de la sesión
        System.out.println("📋 TODOS los atributos en sesión:");
        java.util.Enumeration<String> attributeNames = session.getAttributeNames();
        boolean hasAttributes = false;
        while (attributeNames.hasMoreElements()) {
            String attrName = attributeNames.nextElement();
            Object attrValue = session.getAttribute(attrName);
            System.out.println("   - " + attrName + ": " + attrValue);
            hasAttributes = true;
        }
        if (!hasAttributes) {
            System.out.println("   - ⚠️ NO HAY ATRIBUTOS EN SESIÓN");
        }
        
        // ✅ PERMITIR VER CARRITO AUNQUE EL USUARIO NO ESTÉ LOGUEADO
        if (usuario == null) {
            System.out.println("⚠️  Usuario no en sesión, pero mostrando carrito vacío");
            model.addAttribute("carrito", new ArrayList<>());
            model.addAttribute("usuario", null);
            model.addAttribute("total", 0.0);
            System.out.println("🛒 DEBUG: ===== FIN VER CARRITO (USUARIO NULL) =====");
            return "carrito-ver";
        }

        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
            System.out.println("🆕 Carrito vacío creado");
        }

        // ✅ CALCULAR TOTAL EN EL CONTROLADOR (MÁS EFICIENTE Y SEGURO)
        double total = carrito.stream()
                .mapToDouble(CarritoItem::getSubtotal)
                .sum();

        model.addAttribute("carrito", carrito);
        model.addAttribute("usuario", usuario);
        model.addAttribute("total", total);

        System.out.println("✅ Carrito mostrado para usuario: " + usuario.getNombre() + 
                         " - Items: " + carrito.size() + 
                         " - Total: $" + total);
        System.out.println("🛒 DEBUG: ===== FIN VER CARRITO (ÉXITO) =====");
        return "carrito-ver";
    }

    // 🔹 Agregar producto al carrito
    @PostMapping("/carrito/agregar")
    public String agregarAlCarrito(@RequestParam("codigoProducto") Integer codigoProducto,
                                   @RequestParam(value = "cantidad", defaultValue = "1") Integer cantidad,
                                   HttpSession session, HttpServletRequest request) {

        System.out.println("🛒 DEBUG: ===== INICIO AGREGAR CARRITO =====");
        System.out.println("🔗 URL: " + request.getRequestURL());
        System.out.println("🆔 ID de sesión: " + session.getId());
        System.out.println("🔍 Parámetros: codigoProducto=" + codigoProducto + ", cantidad=" + cantidad);
        
        // ✅ VERIFICACIÓN DE LA SESIÓN
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        System.out.println("👤 Usuario en sesión: " + (usuario != null ? usuario.getNombre() : "NULL"));
        
        if (usuario == null) {
            System.out.println("❌ DEBUG: USUARIO NULL - Redirigiendo a login");
            System.out.println("🛒 DEBUG: ===== FIN AGREGAR CARRITO (REDIRIGIENDO) =====");
            return "redirect:/usuario/login?error=debes_iniciar_sesion_primero";
        }

        // Buscar producto por código
        Optional<Producto> optProducto = productoRepository.findByCodigoProducto(codigoProducto);
        if (optProducto.isEmpty()) {
            System.out.println("❌ Producto no encontrado, código: " + codigoProducto);
            return "redirect:/usuario/productos?error=producto_no_encontrado";
        }

        Producto producto = optProducto.get();
        System.out.println("✅ Producto encontrado: " + producto.getNombre() + " - ID: " + producto.getId());

        // Obtener el carrito de la sesión (o crear uno nuevo)
        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
            System.out.println("🆕 Carrito nuevo creado");
        }

        // Verificar si el producto ya está en el carrito
        boolean existe = false;
        for (CarritoItem item : carrito) {
            if (item.getProducto().getId().equals(producto.getId())) {
                item.setCantidad(item.getCantidad() + cantidad);
                existe = true;
                System.out.println("✅ Producto actualizado en carrito, nueva cantidad: " + item.getCantidad());
                break;
            }
        }

        // Si no existe, lo agregamos
        if (!existe) {
            carrito.add(new CarritoItem(producto, cantidad));
            System.out.println("✅ Nuevo producto agregado al carrito");
        }

        // Guardamos el carrito actualizado en la sesión
        session.setAttribute("carrito", carrito);
        System.out.println("💾 Carrito guardado en sesión, total items: " + carrito.size());

        // Verificar que se guardó correctamente
        List<CarritoItem> carritoVerificado = (List<CarritoItem>) session.getAttribute("carrito");
        System.out.println("✅ Verificación - Carrito en sesión: " + 
                          (carritoVerificado != null ? carritoVerificado.size() + " items" : "NULL"));

        System.out.println("🛒 DEBUG: ===== FIN AGREGAR CARRITO (ÉXITO) =====");
        return "redirect:/usuario/productos?agregado=" + producto.getNombre();
    }

    // 🔹 Vaciar carrito
    @PostMapping("/carrito/vaciar")
    public String vaciarCarrito(HttpSession session, HttpServletRequest request) {
        System.out.println("🛒 DEBUG: ===== VACIAR CARRITO =====");
        System.out.println("🔗 URL: " + request.getRequestURL());
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            System.out.println("❌ Usuario no en sesión");
            return "redirect:/usuario/login";
        }

        session.removeAttribute("carrito");
        System.out.println("✅ Carrito vaciado para usuario: " + usuario.getNombre());
        return "redirect:/carrito/ver";
    }

    // 🔹 Eliminar item específico del carrito
    @PostMapping("/carrito/eliminar")
    public String eliminarDelCarrito(@RequestParam("productoId") Integer productoId,
                                     HttpSession session) {
        System.out.println("🛒 DEBUG: ===== ELIMINAR DEL CARRITO =====");
        System.out.println("🔍 Producto ID a eliminar: " + productoId);
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            System.out.println("❌ Usuario no en sesión");
            return "redirect:/usuario/login";
        }

        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito != null) {
            // Eliminar el producto del carrito
            boolean removido = carrito.removeIf(item -> item.getProducto().getId().equals(productoId));
            if (removido) {
                System.out.println("✅ Producto eliminado del carrito");
                session.setAttribute("carrito", carrito);
            } else {
                System.out.println("⚠️ Producto no encontrado en carrito");
            }
        }

        return "redirect:/carrito/ver";
    }

    // 🔹 Actualizar cantidad de un item en el carrito
    @PostMapping("/carrito/actualizar")
    public String actualizarCantidad(@RequestParam("productoId") Integer productoId,
                                     @RequestParam("cantidad") Integer cantidad,
                                     HttpSession session) {
        System.out.println("🛒 DEBUG: ===== ACTUALIZAR CANTIDAD =====");
        System.out.println("🔍 Producto ID: " + productoId + ", Nueva cantidad: " + cantidad);
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            System.out.println("❌ Usuario no en sesión");
            return "redirect:/usuario/login";
        }

        if (cantidad <= 0) {
            System.out.println("❌ Cantidad inválida: " + cantidad);
            return "redirect:/carrito/ver?error=cantidad_invalida";
        }

        List<CarritoItem> carrito = (List<CarritoItem>) session.getAttribute("carrito");
        if (carrito != null) {
            // Buscar y actualizar el producto
            for (CarritoItem item : carrito) {
                if (item.getProducto().getId().equals(productoId)) {
                    item.setCantidad(cantidad);
                    System.out.println("✅ Cantidad actualizada: " + cantidad);
                    session.setAttribute("carrito", carrito);
                    break;
                }
            }
        }

        return "redirect:/carrito/ver";
    }
}