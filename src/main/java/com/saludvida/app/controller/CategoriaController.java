package com.saludvida.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.saludvida.app.model.dto.request.CategoriaRequestDTO;
import com.saludvida.app.services.CategoriaApiService;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {
    private final CategoriaApiService service;
    public CategoriaController(CategoriaApiService service) { this.service = service; }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", service.listar());
        model.addAttribute("request", new CategoriaRequestDTO());
        return "inventario/categorias";
    }

    @PostMapping
    public String crear(@ModelAttribute("request") CategoriaRequestDTO request) {
        service.crear(request);
        return "redirect:/categorias";
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable long id) {
        service.desactivar(id);
        return "redirect:/categorias";
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable long id) {
        service.activar(id);
        return "redirect:/categorias";
    }
}
