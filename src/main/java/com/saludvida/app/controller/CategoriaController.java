package com.saludvida.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.saludvida.app.model.dto.request.CategoriaRequestDTO;
import com.saludvida.app.model.dto.response.CategoriaResponseDTO;
import com.saludvida.app.services.ICategoriaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final ICategoriaService service;

    public CategoriaController(ICategoriaService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        cargarModeloBase(model);

        if (!model.containsAttribute("request")) {
            model.addAttribute("request", new CategoriaRequestDTO());
        }

        if (!model.containsAttribute("editando")) {
            model.addAttribute("editando", false);
        }

        return "inventario/categorias";
    }

    @PostMapping
    public String crear(
            @Valid @ModelAttribute("request") CategoriaRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            return "inventario/categorias";
        }

        try {
            request.setActivo(true);
            service.crear(request);
            redirectAttributes.addFlashAttribute("mensaje", "Categoria creada correctamente");
            return "redirect:/categorias";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            model.addAttribute("error", "No se pudo crear la categoria. Verifica que el nombre no este duplicado.");
            return "inventario/categorias";
        }
    }

    @GetMapping("/{id}/editar")
    public String mostrarEditar(@PathVariable long id, Model model, RedirectAttributes redirectAttributes) {
        var categoria = service.buscarPorId(id);

        if (categoria.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Categoria no encontrada");
            return "redirect:/categorias";
        }

        CategoriaResponseDTO response = categoria.get();
        CategoriaRequestDTO request = new CategoriaRequestDTO();
        request.setNombre(response.getNombre());
        request.setDescripcion(response.getDescripcion());
        request.setActivo(response.getActivo());

        cargarModeloBase(model);
        model.addAttribute("request", request);
        model.addAttribute("editando", true);
        model.addAttribute("editId", id);

        return "inventario/categorias";
    }

    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable long id,
            @Valid @ModelAttribute("request") CategoriaRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            return "inventario/categorias";
        }

        try {
            service.actualizar(id, request);
            redirectAttributes.addFlashAttribute("mensaje", "Categoria actualizada correctamente");
            return "redirect:/categorias";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            model.addAttribute("error", "No se pudo actualizar la categoria.");
            return "inventario/categorias";
        }
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable long id, RedirectAttributes redirectAttributes) {
        service.desactivar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Categoria desactivada. No se elimino fisicamente por auditoria.");
        return "redirect:/categorias";
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable long id, RedirectAttributes redirectAttributes) {
        service.activar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Categoria activada correctamente");
        return "redirect:/categorias";
    }

    private void cargarModeloBase(Model model) {
        model.addAttribute("categorias", service.listar());
    }
}
