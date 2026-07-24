package com.saludvida.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.saludvida.app.model.dto.request.ProveedorRequestDTO;
import com.saludvida.app.services.ProveedorApiService;

@Controller
@RequestMapping("/proveedores")
public class ProveedorController {
    private final ProveedorApiService service;
    public ProveedorController(ProveedorApiService service) { this.service = service; }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("proveedores", service.listar());
        model.addAttribute("request", new ProveedorRequestDTO());
        return "inventario/proveedores";
    }

    @PostMapping
    public String crear(@ModelAttribute("request") ProveedorRequestDTO request) {
        service.crear(request);
        return "redirect:/proveedores";
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable long id) {
        service.desactivar(id);
        return "redirect:/proveedores";
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable long id) {
        service.activar(id);
        return "redirect:/proveedores";
    }
}
