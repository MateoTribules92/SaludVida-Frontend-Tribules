package com.saludvida.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.saludvida.app.model.dto.request.PedidoRequestDTO;
import com.saludvida.app.model.enums.EstadoPedido;
import com.saludvida.app.services.PedidoApiService;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidoApiService service;
    public PedidoController(PedidoApiService service) { this.service = service; }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pedidos", service.listar());
        model.addAttribute("request", new PedidoRequestDTO());
        model.addAttribute("estados", EstadoPedido.values());
        return "ventas-pedidos/pedidos";
    }

    @PostMapping
    public String crear(@ModelAttribute("request") PedidoRequestDTO request) {
        service.crear(request);
        return "redirect:/pedidos";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable long id, @RequestParam EstadoPedido estado) {
        service.cambiarEstado(id, estado);
        return "redirect:/pedidos";
    }
}
