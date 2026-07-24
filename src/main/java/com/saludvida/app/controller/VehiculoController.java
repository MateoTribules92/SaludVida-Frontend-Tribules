package com.saludvida.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.saludvida.app.model.dto.request.VehiculoRequestDTO;
import com.saludvida.app.services.VehiculoApiService;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoController {
    private final VehiculoApiService service;
    public VehiculoController(VehiculoApiService service) { this.service = service; }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("vehiculos", service.listar());
        model.addAttribute("request", new VehiculoRequestDTO());
        return "distribucion/vehiculos";
    }

    @PostMapping
    public String crear(@ModelAttribute("request") VehiculoRequestDTO request) {
        service.crear(request);
        return "redirect:/vehiculos";
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable long id) {
        service.desactivar(id);
        return "redirect:/vehiculos";
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable long id) {
        service.activar(id);
        return "redirect:/vehiculos";
    }
}
