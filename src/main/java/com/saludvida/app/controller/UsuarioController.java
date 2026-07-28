package com.saludvida.app.controller;

import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.saludvida.app.model.dto.request.UsuarioRequestDTO;
import com.saludvida.app.model.dto.response.FarmaciaResponseDTO;
import com.saludvida.app.model.dto.response.RolResponseDTO;
import com.saludvida.app.model.dto.response.UsuarioResponseDTO;
import com.saludvida.app.services.IFarmaciaService;
import com.saludvida.app.services.IRolService;
import com.saludvida.app.services.IUsuarioService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final IUsuarioService service;
    private final IRolService rolService;
    private final IFarmaciaService farmaciaService;

    public UsuarioController(IUsuarioService service, IRolService rolService, IFarmaciaService farmaciaService) {
        this.service = service;
        this.rolService = rolService;
        this.farmaciaService = farmaciaService;
    }

    @GetMapping
    public String listar(Model model) {
        cargarModeloBase(model);

        if (!model.containsAttribute("request")) {
            model.addAttribute("request", new UsuarioRequestDTO());
        }

        if (!model.containsAttribute("editando")) {
            model.addAttribute("editando", false);
        }

        return "administracion/usuarios";
    }

    @PostMapping
    public String crear(
            @Valid @ModelAttribute("request") UsuarioRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            return "administracion/usuarios";
        }

        try {
            request.setActivo(true);
            service.crear(request);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario creado correctamente");
            return "redirect:/usuarios";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            model.addAttribute("error", "No se pudo crear el usuario. Verifica que el correo no esté duplicado y que el rol/farmacia existan.");
            return "administracion/usuarios";
        }
    }

    @GetMapping("/{id}/editar")
    public String mostrarEditar(@PathVariable long id, Model model, RedirectAttributes redirectAttributes) {
        var usuario = service.buscarPorId(id);

        if (usuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/usuarios";
        }

        UsuarioResponseDTO response = usuario.get();
        UsuarioRequestDTO request = new UsuarioRequestDTO();
        request.setIdRol(response.getIdRol());
        request.setIdFarmacia(response.getIdFarmacia());
        request.setNombres(response.getNombres());
        request.setCorreo(response.getCorreo());
        request.setActivo(response.getActivo());

        cargarModeloBase(model);
        model.addAttribute("request", request);
        model.addAttribute("editando", true);
        model.addAttribute("editId", id);

        return "administracion/usuarios";
    }

    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable long id,
            @Valid @ModelAttribute("request") UsuarioRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            return "administracion/usuarios";
        }

        try {
            service.actualizar(id, request);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
            return "redirect:/usuarios";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            model.addAttribute("error", "No se pudo actualizar el usuario.");
            return "administracion/usuarios";
        }
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable long id, RedirectAttributes redirectAttributes) {
        service.desactivar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario desactivado. No se eliminó físicamente por auditoría.");
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable long id, RedirectAttributes redirectAttributes) {
        service.activar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario activado correctamente");
        return "redirect:/usuarios";
    }

    private void cargarModeloBase(Model model) {
        var usuarios = service.listar();
        var todosRoles = rolService.listar();
        var todasFarmacias = farmaciaService.listar();
        var rolesActivos = todosRoles.stream()
                .filter(r -> Boolean.TRUE.equals(r.getActivo()))
                .toList();
        var farmaciasActivas = todasFarmacias.stream()
                .filter(f -> Boolean.TRUE.equals(f.getActivo()))
                .toList();

        var nombresRoles = todosRoles.stream()
                .collect(Collectors.toMap(
                        RolResponseDTO::getIdRol,
                        RolResponseDTO::getNombre,
                        (actual, repetido) -> actual));

        var nombresFarmacias = todasFarmacias.stream()
                .collect(Collectors.toMap(
                        FarmaciaResponseDTO::getIdFarmacia,
                        f -> f.getNombre() + " - " + f.getZona(),
                        (actual, repetido) -> actual));

        long activos = usuarios.stream()
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .count();

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("roles", rolesActivos);
        model.addAttribute("farmacias", farmaciasActivas);
        model.addAttribute("nombresRoles", nombresRoles);
        model.addAttribute("nombresFarmacias", nombresFarmacias);
        model.addAttribute("usuariosActivos", activos);
    }
}
