package com.saludvida.app.controller;

import java.util.HashMap;
import java.util.Map;
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
import com.saludvida.app.model.enums.TipoMovimiento;
import com.saludvida.app.services.IFarmaciaService;
import com.saludvida.app.services.IInventarioService;
import com.saludvida.app.services.IMovimientoInventarioService;
import com.saludvida.app.services.IProductoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/movimientos")
public class MovimientoInventarioController {

    private final IMovimientoInventarioService service;
    private final IInventarioService inventarioService;
    private final IFarmaciaService farmaciaService;
    private final IProductoService productoService;

    public MovimientoInventarioController(
            IMovimientoInventarioService service,
            IInventarioService inventarioService,
            IFarmaciaService farmaciaService,
            IProductoService productoService) {
        this.service = service;
        this.inventarioService = inventarioService;
        this.farmaciaService = farmaciaService;
        this.productoService = productoService;
    }

    @GetMapping
    public String listar(Model model) {
        cargarModeloBase(model);

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
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarModeloBase(model);
            return "inventario/movimientos";
        }

        try {
            if (request.getIdUsuario() != null && request.getIdUsuario() <= 0) {
                request.setIdUsuario(null);
            }

            service.registrar(request);
            redirectAttributes.addFlashAttribute("mensaje", "Movimiento registrado y stock actualizado correctamente");
            return "redirect:/movimientos";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("error", "No se pudo registrar el movimiento. Verifica el stock disponible y los datos seleccionados.");
            return "inventario/movimientos";
        }
    }

    private void cargarModeloBase(Model model) {
        var inventarios = inventarioService.listar();
        var movimientos = service.listar();
        var farmacias = farmaciaService.listar();
        var productos = productoService.listar();

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
        model.addAttribute("entradas", entradas);
        model.addAttribute("salidas", salidas);
        model.addAttribute("ajustes", ajustes);
    }
}
