package com.saludvida.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.saludvida.app.model.dto.request.ProductoRequestDTO;
import com.saludvida.app.services.ProductoApiService;

@Controller
@RequestMapping("/productos")
public class ProductoController {
    private final ProductoApiService service;
    public ProductoController(ProductoApiService service) { this.service = service; }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", service.listar());
        model.addAttribute("request", new ProductoRequestDTO());
        return "inventario/productos";
    }

    @PostMapping
    public String crear(@ModelAttribute("request") ProductoRequestDTO request) {
        service.crear(request);
        return "redirect:/productos";
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable long id) {
        service.desactivar(id);
        return "redirect:/productos";
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable long id) {
        service.activar(id);
        return "redirect:/productos";
    }
}
