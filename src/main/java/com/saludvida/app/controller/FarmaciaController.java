package com.saludvida.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.saludvida.app.model.dto.request.FarmaciaRequestDTO;
import com.saludvida.app.services.FarmaciaApiService;

@Controller
@RequestMapping("/farmacias")
public class FarmaciaController {
    private final FarmaciaApiService service;
    public FarmaciaController(FarmaciaApiService service) { this.service = service; }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("farmacias", service.listar());
        model.addAttribute("request", new FarmaciaRequestDTO());
        return "administracion/farmacias";
    }

    @PostMapping
    public String crear(@ModelAttribute("request") FarmaciaRequestDTO request) {
        service.crear(request);
        return "redirect:/farmacias";
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable long id) {
        service.desactivar(id);
        return "redirect:/farmacias";
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable long id) {
        service.activar(id);
        return "redirect:/farmacias";
    }
}
