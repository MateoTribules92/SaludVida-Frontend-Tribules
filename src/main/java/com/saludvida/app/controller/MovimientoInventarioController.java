package com.saludvida.app.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.saludvida.app.model.dto.request.MovimientoInventarioRequestDTO;
import com.saludvida.app.model.dto.response.FarmaciaResponseDTO;
import com.saludvida.app.model.dto.response.InventarioResponseDTO;
import com.saludvida.app.model.dto.response.ProductoResponseDTO;
import com.saludvida.app.model.dto.response.UsuarioResponseDTO;
import com.saludvida.app.model.enums.TipoMovimiento;
import com.saludvida.app.services.IFarmaciaService;
import com.saludvida.app.services.IInventarioService;
import com.saludvida.app.services.IMovimientoInventarioService;
import com.saludvida.app.services.IProductoService;
import com.saludvida.app.services.IUsuarioService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/movimientos")
public class MovimientoInventarioController {

    private final IMovimientoInventarioService service;
    private final IInventarioService inventarioService;
    private final IFarmaciaService farmaciaService;
    private final IProductoService productoService;
    private final IUsuarioService usuarioService;

    public MovimientoInventarioController(
            IMovimientoInventarioService service,
            IInventarioService inventarioService,
            IFarmaciaService farmaciaService,
            IProductoService productoService,
            IUsuarioService usuarioService) {
        this.service = service;
        this.inventarioService = inventarioService;
        this.farmaciaService = farmaciaService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {
        cargarModeloBase(model, session);

        if (!model.containsAttribute("request")) {
            model.addAttribute("request", new MovimientoInventarioRequestDTO());
        }

        return "inventario/movimientos";
    }

    @PostMapping
    public String registrar(
            @Valid @ModelAttribute("request") MovimientoInventarioRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (result.hasErrors()) {
            cargarModeloBase(model, session);
            return "inventario/movimientos";
        }

        try {
            if (!puedeGestionarInventario(request.getIdInventario(), session)) {
                model.addAttribute("error", "No puedes registrar movimientos sobre inventario de otra farmacia.");
                cargarModeloBase(model, session);
                return "inventario/movimientos";
            }

            request.setIdUsuario(obtenerIdUsuarioSesion(session));

            service.registrar(request);
            redirectAttributes.addFlashAttribute("mensaje", "Movimiento registrado y stock actualizado correctamente");
            return "redirect:/movimientos";
        } catch (Exception e) {
            cargarModeloBase(model, session);
            model.addAttribute("error", "No se pudo registrar el movimiento. Verifica el stock disponible y los datos seleccionados.");
            return "inventario/movimientos";
        }
    }

    private void cargarModeloBase(Model model, HttpSession session) {
        var inventarios = inventarioService.listar();
        var movimientos = service.listar();
        var farmacias = farmaciaService.listar();
        var productos = productoService.listar();
        var usuarios = usuarioService.listar();

        Long idFarmaciaSesion = obtenerIdFarmaciaSesion(session);
        boolean esAdmin = esAdmin(session);

        if (!esAdmin && idFarmaciaSesion != null) {
            inventarios = inventarios.stream()
                    .filter(i -> i.getIdFarmacia() == idFarmaciaSesion)
                    .toList();

            Set<Long> idsInventarioPermitidos = inventarios.stream()
                    .map(InventarioResponseDTO::getIdInventario)
                    .collect(Collectors.toSet());

            movimientos = movimientos.stream()
                    .filter(m -> idsInventarioPermitidos.contains(m.getIdInventario()))
                    .toList();
        }

        Map<Long, String> nombresFarmacias = farmacias.stream()
                .collect(Collectors.toMap(
                        FarmaciaResponseDTO::getIdFarmacia,
                        FarmaciaResponseDTO::getNombre,
                        (actual, repetido) -> actual));

        Map<Long, ProductoResponseDTO> productosPorId = productos.stream()
                .collect(Collectors.toMap(
                        ProductoResponseDTO::getIdProducto,
                        producto -> producto,
                        (actual, repetido) -> actual));

        Map<Long, String> nombresUsuarios = usuarios.stream()
                .collect(Collectors.toMap(
                        UsuarioResponseDTO::getIdUsuario,
                        UsuarioResponseDTO::getNombres,
                        (actual, repetido) -> actual));

        Map<Long, String> nombresInventarios = new HashMap<>();
        for (InventarioResponseDTO inventario : inventarios) {
            ProductoResponseDTO producto = productosPorId.get(inventario.getIdProducto());
            String nombreProducto = producto != null ? producto.getNombre() : "Producto no encontrado";
            String codigoProducto = producto != null ? producto.getCodigo() : "S/C";
            String nombreFarmacia = nombresFarmacias.getOrDefault(inventario.getIdFarmacia(), "Farmacia no encontrada");

            nombresInventarios.put(
                    inventario.getIdInventario(),
                    nombreFarmacia + " - " + nombreProducto + " (" + codigoProducto + ")");
        }

        long entradas = movimientos.stream()
                .filter(m -> TipoMovimiento.ENTRADA.equals(m.getTipoMovimiento()))
                .count();
        long salidas = movimientos.stream()
                .filter(m -> TipoMovimiento.SALIDA.equals(m.getTipoMovimiento()))
                .count();
        long ajustes = movimientos.stream()
                .filter(m -> TipoMovimiento.AJUSTE.equals(m.getTipoMovimiento()))
                .count();

        model.addAttribute("inventarios", inventarios);
        model.addAttribute("movimientos", movimientos);
        model.addAttribute("tiposMovimiento", TipoMovimiento.values());
        model.addAttribute("nombresInventarios", nombresInventarios);
        model.addAttribute("nombresUsuarios", nombresUsuarios);
        model.addAttribute("usuarioMovimiento", obtenerNombreUsuarioSesion(session));
        model.addAttribute("entradas", entradas);
        model.addAttribute("salidas", salidas);
        model.addAttribute("ajustes", ajustes);
    }

    private boolean puedeGestionarInventario(long idInventario, HttpSession session) {
        if (esAdmin(session)) {
            return true;
        }

        Long idFarmaciaSesion = obtenerIdFarmaciaSesion(session);

        if (idFarmaciaSesion == null) {
            return false;
        }

        return inventarioService.buscarPorId(idInventario)
                .map(i -> i.getIdFarmacia() == idFarmaciaSesion)
                .orElse(false);
    }

    private boolean esAdmin(HttpSession session) {
        Object rol = session.getAttribute("rolUsuario");
        return "ADMINISTRADOR".equals(rol) || "ADMIN".equals(rol);
    }

    private Long obtenerIdFarmaciaSesion(HttpSession session) {
        Object valor = session.getAttribute("idFarmacia");

        if (valor instanceof Number number) {
            return number.longValue();
        }

        if (valor instanceof String texto && !texto.trim().isEmpty()) {
            try {
                return Long.parseLong(texto.trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    private Long obtenerIdUsuarioSesion(HttpSession session) {
        Object valor = session.getAttribute("idUsuario");

        if (valor instanceof Number number) {
            return number.longValue();
        }

        if (valor instanceof String texto && !texto.trim().isEmpty()) {
            try {
                return Long.parseLong(texto.trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    private String obtenerNombreUsuarioSesion(HttpSession session) {
        Object valor = session.getAttribute("nombreUsuario");
        return valor != null ? valor.toString() : "Usuario logueado";
    }
}
