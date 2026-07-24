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

import com.saludvida.app.model.dto.request.ProveedorRequestDTO;
import com.saludvida.app.model.dto.response.ProveedorResponseDTO;
import com.saludvida.app.services.IProveedorService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/proveedores")
public class ProveedorController {

    private final IProveedorService service;

    public ProveedorController(IProveedorService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        cargarModeloBase(model);

        if (!model.containsAttribute("request")) {
            model.addAttribute("request", new ProveedorRequestDTO());
        }

        if (!model.containsAttribute("editando")) {
            model.addAttribute("editando", false);
        }

        return "inventario/proveedores";
    }

    @PostMapping
    public String crear(
            @Valid @ModelAttribute("request") ProveedorRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            return "inventario/proveedores";
        }

        try {
            request.setActivo(true);
            service.crear(request);
            redirectAttributes.addFlashAttribute("mensaje", "Proveedor creado correctamente");
            return "redirect:/proveedores";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            model.addAttribute("error", "No se pudo crear el proveedor. Verifica que el RUC o correo no esten duplicados.");
            return "inventario/proveedores";
        }
    }

    @GetMapping("/{id}/editar")
    public String mostrarEditar(@PathVariable long id, Model model, RedirectAttributes redirectAttributes) {
        var proveedor = service.buscarPorId(id);

        if (proveedor.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Proveedor no encontrado");
            return "redirect:/proveedores";
        }

        ProveedorResponseDTO response = proveedor.get();
        ProveedorRequestDTO request = new ProveedorRequestDTO();
        request.setNombre(response.getNombre());
        request.setRuc(response.getRuc());
        request.setTelefono(response.getTelefono());
        request.setCorreo(response.getCorreo());
        request.setDireccion(response.getDireccion());
        request.setActivo(response.getActivo());

        cargarModeloBase(model);
        model.addAttribute("request", request);
        model.addAttribute("editando", true);
        model.addAttribute("editId", id);

        return "inventario/proveedores";
    }

    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable long id,
            @Valid @ModelAttribute("request") ProveedorRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            return "inventario/proveedores";
        }

        try {
            service.actualizar(id, request);
            redirectAttributes.addFlashAttribute("mensaje", "Proveedor actualizado correctamente");
            return "redirect:/proveedores";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            model.addAttribute("error", "No se pudo actualizar el proveedor.");
            return "inventario/proveedores";
        }
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable long id, RedirectAttributes redirectAttributes) {
        service.desactivar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Proveedor desactivado. No se elimino fisicamente por auditoria.");
        return "redirect:/proveedores";
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable long id, RedirectAttributes redirectAttributes) {
        service.activar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Proveedor activado correctamente");
        return "redirect:/proveedores";
    }

    private void cargarModeloBase(Model model) {
        model.addAttribute("proveedores", service.listar());
    }
}
