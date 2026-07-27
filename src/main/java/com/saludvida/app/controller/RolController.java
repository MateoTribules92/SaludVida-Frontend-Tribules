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

import com.saludvida.app.model.dto.request.RolRequestDTO;
import com.saludvida.app.model.dto.response.RolResponseDTO;
import com.saludvida.app.services.IRolService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/roles")
public class RolController {

    private final IRolService service;

    public RolController(IRolService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        cargarModeloBase(model);

        if (!model.containsAttribute("request")) {
            model.addAttribute("request", new RolRequestDTO());
        }

        if (!model.containsAttribute("editando")) {
            model.addAttribute("editando", false);
        }

        return "administracion/roles";
    }

    @PostMapping
    public String crear(
            @Valid @ModelAttribute("request") RolRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        normalizarCodigo(request);

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            return "administracion/roles";
        }

        try {
            request.setActivo(true);
            service.crear(request);
            redirectAttributes.addFlashAttribute("mensaje", "Rol creado correctamente");
            return "redirect:/roles";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            model.addAttribute("error", "No se pudo crear el rol. Verifica que el código no esté duplicado.");
            return "administracion/roles";
        }
    }

    @GetMapping("/{id}/editar")
    public String mostrarEditar(@PathVariable long id, Model model, RedirectAttributes redirectAttributes) {
        var rol = service.buscarPorId(id);

        if (rol.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Rol no encontrado");
            return "redirect:/roles";
        }

        RolResponseDTO response = rol.get();
        RolRequestDTO request = new RolRequestDTO();
        request.setCodigo(response.getCodigo());
        request.setNombre(response.getNombre());
        request.setDescripcion(response.getDescripcion());
        request.setActivo(response.getActivo());

        cargarModeloBase(model);
        model.addAttribute("request", request);
        model.addAttribute("editando", true);
        model.addAttribute("editId", id);

        return "administracion/roles";
    }

    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable long id,
            @Valid @ModelAttribute("request") RolRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        normalizarCodigo(request);

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            return "administracion/roles";
        }

        try {
            service.actualizar(id, request);
            redirectAttributes.addFlashAttribute("mensaje", "Rol actualizado correctamente");
            return "redirect:/roles";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            model.addAttribute("error", "No se pudo actualizar el rol.");
            return "administracion/roles";
        }
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable long id, RedirectAttributes redirectAttributes) {
        service.desactivar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Rol desactivado. No se eliminó físicamente por auditoría.");
        return "redirect:/roles";
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable long id, RedirectAttributes redirectAttributes) {
        service.activar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Rol activado correctamente");
        return "redirect:/roles";
    }

    private void cargarModeloBase(Model model) {
        var roles = service.listar();
        long activos = roles.stream()
                .filter(r -> Boolean.TRUE.equals(r.getActivo()))
                .count();

        model.addAttribute("roles", roles);
        model.addAttribute("rolesActivos", activos);
    }

    private void normalizarCodigo(RolRequestDTO request) {
        if (request.getCodigo() != null) {
            request.setCodigo(request.getCodigo().trim().toUpperCase());
        }
    }
}
