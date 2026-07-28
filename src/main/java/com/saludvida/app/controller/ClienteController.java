package com.saludvida.app.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.saludvida.app.model.dto.request.ClienteRequestDTO;
import com.saludvida.app.model.dto.response.ClienteResponseDTO;
import com.saludvida.app.model.enums.ClasificacionCliente;
import com.saludvida.app.model.enums.TipoCliente;
import com.saludvida.app.services.IClienteService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private static final List<String> ZONAS = List.of("Norte", "Centro", "Sur", "Valle");

    private final IClienteService service;

    public ClienteController(IClienteService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        cargarModeloBase(model);

        if (!model.containsAttribute("request")) {
            model.addAttribute("request", new ClienteRequestDTO());
        }

        if (!model.containsAttribute("editando")) {
            model.addAttribute("editando", false);
        }

        return "ventas-pedidos/clientes";
    }

    @PostMapping
    public String crear(
            @Valid @ModelAttribute("request") ClienteRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            return "ventas-pedidos/clientes";
        }

        try {
            request.setActivo(true);
            service.crear(request);
            redirectAttributes.addFlashAttribute("mensaje", "Cliente creado correctamente");
            return "redirect:/clientes";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            model.addAttribute("error", "No se pudo crear el cliente. Verifica que la identificación o correo no estén duplicados.");
            return "ventas-pedidos/clientes";
        }
    }

    @GetMapping("/{id}/editar")
    public String mostrarEditar(@PathVariable long id, Model model, RedirectAttributes redirectAttributes) {
        var cliente = service.buscarPorId(id);

        if (cliente.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cliente no encontrado");
            return "redirect:/clientes";
        }

        ClienteResponseDTO response = cliente.get();
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setTipoCliente(response.getTipoCliente());
        request.setIdentificacion(response.getIdentificacion());
        request.setNombres(response.getNombres());
        request.setCorreo(response.getCorreo());
        request.setTelefono(response.getTelefono());
        request.setDireccion(response.getDireccion());
        request.setCiudad(response.getCiudad());
        request.setZona(response.getZona());
        request.setLatitud(response.getLatitud());
        request.setLongitud(response.getLongitud());
        request.setClasificacion(response.getClasificacion());
        request.setActivo(response.getActivo());

        cargarModeloBase(model);
        model.addAttribute("request", request);
        model.addAttribute("editando", true);
        model.addAttribute("editId", id);

        return "ventas-pedidos/clientes";
    }

    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable long id,
            @Valid @ModelAttribute("request") ClienteRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            return "ventas-pedidos/clientes";
        }

        try {
            service.actualizar(id, request);
            redirectAttributes.addFlashAttribute("mensaje", "Cliente actualizado correctamente");
            return "redirect:/clientes";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            model.addAttribute("error", "No se pudo actualizar el cliente.");
            return "ventas-pedidos/clientes";
        }
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable long id, RedirectAttributes redirectAttributes) {
        service.desactivar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Cliente desactivado. No se eliminó físicamente por auditoría.");
        return "redirect:/clientes";
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable long id, RedirectAttributes redirectAttributes) {
        service.activar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Cliente activado correctamente");
        return "redirect:/clientes";
    }

    private void cargarModeloBase(Model model) {
        var clientes = service.listar();

        long activos = clientes.stream()
                .filter(c -> Boolean.TRUE.equals(c.getActivo()))
                .count();

        long frecuentes = clientes.stream()
                .filter(c -> ClasificacionCliente.FRECUENTE.equals(c.getClasificacion()))
                .count();

        model.addAttribute("clientes", clientes);
        model.addAttribute("tiposCliente", TipoCliente.values());
        model.addAttribute("clasificacionesCliente", ClasificacionCliente.values());
        model.addAttribute("zonas", ZONAS);
        model.addAttribute("clientesActivos", activos);
        model.addAttribute("clientesFrecuentes", frecuentes);
    }
}
