package com.saludvida.app.controller;

import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.saludvida.app.model.dto.request.InventarioRequestDTO;
import com.saludvida.app.model.dto.response.FarmaciaResponseDTO;
import com.saludvida.app.model.dto.response.InventarioResponseDTO;
import com.saludvida.app.model.dto.response.ProductoResponseDTO;
import com.saludvida.app.services.IFarmaciaService;
import com.saludvida.app.services.IInventarioService;
import com.saludvida.app.services.IProductoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/inventarios")
public class InventarioController {

    private final IInventarioService service;
    private final IFarmaciaService farmaciaService;
    private final IProductoService productoService;

    public InventarioController(
            IInventarioService service,
            IFarmaciaService farmaciaService,
            IProductoService productoService) {
        this.service = service;
        this.farmaciaService = farmaciaService;
        this.productoService = productoService;
    }

    @GetMapping
    public String listar(Model model) {
        cargarModeloBase(model);

        if (!model.containsAttribute("request")) {
            model.addAttribute("request", new InventarioRequestDTO());
        }

        if (!model.containsAttribute("editando")) {
            model.addAttribute("editando", false);
        }

        return "inventario/stockporfamacia";
    }

    @PostMapping
    public String guardar(
            @Valid @ModelAttribute("request") InventarioRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            return "inventario/stockporfamacia";
        }

        try {
            service.guardar(request);
            redirectAttributes.addFlashAttribute("mensaje", "Inventario registrado correctamente");
            return "redirect:/inventarios";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            model.addAttribute("error", "No se pudo registrar el inventario. Verifica que no exista ya la combinacion farmacia/producto.");
            return "inventario/stockporfamacia";
        }
    }

    @GetMapping("/{id}/editar")
    public String mostrarEditar(@PathVariable long id, Model model, RedirectAttributes redirectAttributes) {
        var inventario = service.buscarPorId(id);

        if (inventario.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Inventario no encontrado");
            return "redirect:/inventarios";
        }

        InventarioResponseDTO response = inventario.get();
        InventarioRequestDTO request = new InventarioRequestDTO();
        request.setIdFarmacia(response.getIdFarmacia());
        request.setIdProducto(response.getIdProducto());
        request.setStock(response.getStock());
        request.setStockMinimo(response.getStockMinimo());

        cargarModeloBase(model);
        model.addAttribute("request", request);
        model.addAttribute("editando", true);
        model.addAttribute("editId", id);

        return "inventario/stockporfamacia";
    }

    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable long id,
            @Valid @ModelAttribute("request") InventarioRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            return "inventario/stockporfamacia";
        }

        try {
            service.actualizar(id, request);
            redirectAttributes.addFlashAttribute("mensaje", "Inventario actualizado correctamente");
            return "redirect:/inventarios";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            model.addAttribute("error", "No se pudo actualizar el inventario. Verifica que farmacia y producto existan.");
            return "inventario/stockporfamacia";
        }
    }

    private void cargarModeloBase(Model model) {
        var inventarios = service.listar();
        var todasFarmacias = farmaciaService.listar();
        var todosProductos = productoService.listar();

        var farmaciasActivas = todasFarmacias.stream()
                .filter(f -> Boolean.TRUE.equals(f.getActivo()))
                .toList();

        var productosActivos = todosProductos.stream()
                .filter(p -> Boolean.TRUE.equals(p.getActivo()))
                .toList();

        var nombresFarmacias = todasFarmacias.stream()
                .collect(Collectors.toMap(
                        FarmaciaResponseDTO::getIdFarmacia,
                        FarmaciaResponseDTO::getNombre,
                        (nombreActual, nombreRepetido) -> nombreActual
                ));

        var nombresProductos = todosProductos.stream()
                .collect(Collectors.toMap(
                        ProductoResponseDTO::getIdProducto,
                        ProductoResponseDTO::getNombre,
                        (nombreActual, nombreRepetido) -> nombreActual
                ));

        long totalStockBajo = inventarios.stream()
                .filter(i -> i.getStock() != null && i.getStockMinimo() != null && i.getStock() <= i.getStockMinimo())
                .count();

        model.addAttribute("inventarios", inventarios);
        model.addAttribute("farmacias", farmaciasActivas);
        model.addAttribute("productos", productosActivos);
        model.addAttribute("nombresFarmacias", nombresFarmacias);
        model.addAttribute("nombresProductos", nombresProductos);
        model.addAttribute("totalStockBajo", totalStockBajo);
    }
}
