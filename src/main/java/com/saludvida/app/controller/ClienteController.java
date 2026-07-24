package com.saludvida.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.saludvida.app.model.dto.request.ClienteRequestDTO;
import com.saludvida.app.services.ClienteApiService;

@Controller
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteApiService service;
    public ClienteController(ClienteApiService service) { this.service = service; }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", service.listar());
        model.addAttribute("request", new ClienteRequestDTO());
        return "ventas-pedidos/clientes";
    }

    @PostMapping
    public String crear(@ModelAttribute("request") ClienteRequestDTO request) {
        service.crear(request);
        return "redirect:/clientes";
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable long id) {
        service.desactivar(id);
        return "redirect:/clientes";
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable long id) {
        service.activar(id);
        return "redirect:/clientes";
    }
}
