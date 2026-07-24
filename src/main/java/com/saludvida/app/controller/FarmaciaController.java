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

import com.saludvida.app.model.dto.request.FarmaciaRequestDTO;
import com.saludvida.app.model.dto.response.FarmaciaResponseDTO;
import com.saludvida.app.services.IFarmaciaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/farmacias")
public class FarmaciaController {

    private final IFarmaciaService service;

    public FarmaciaController(IFarmaciaService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        cargarModeloBase(model);

        if (!model.containsAttribute("request")) {
            model.addAttribute("request", new FarmaciaRequestDTO());
        }

        if (!model.containsAttribute("editando")) {
            model.addAttribute("editando", false);
        }

        return "administracion/farmacias";
    }

    @PostMapping
    public String crear(
            @Valid @ModelAttribute("request") FarmaciaRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            return "administracion/farmacias";
        }

        try {
            request.setActivo(true);
            service.crear(request);
            redirectAttributes.addFlashAttribute("mensaje", "Farmacia creada correctamente");
            return "redirect:/farmacias";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            model.addAttribute("error", "No se pudo crear la farmacia. Verifica los datos ingresados.");
            return "administracion/farmacias";
        }
    }

    @GetMapping("/{id}/editar")
    public String mostrarEditar(@PathVariable long id, Model model, RedirectAttributes redirectAttributes) {
        var farmacia = service.buscarPorId(id);

        if (farmacia.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Farmacia no encontrada");
            return "redirect:/farmacias";
        }

        FarmaciaResponseDTO response = farmacia.get();
        FarmaciaRequestDTO request = new FarmaciaRequestDTO();
        request.setNombre(response.getNombre());
        request.setDireccion(response.getDireccion());
        request.setCiudad(response.getCiudad());
        request.setZona(response.getZona());
        request.setLatitud(response.getLatitud());
        request.setLongitud(response.getLongitud());
        request.setActivo(response.getActivo());

        cargarModeloBase(model);
        model.addAttribute("request", request);
        model.addAttribute("editando", true);
        model.addAttribute("editId", id);

        return "administracion/farmacias";
    }

    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable long id,
            @Valid @ModelAttribute("request") FarmaciaRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            return "administracion/farmacias";
        }

        try {
            service.actualizar(id, request);
            redirectAttributes.addFlashAttribute("mensaje", "Farmacia actualizada correctamente");
            return "redirect:/farmacias";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            model.addAttribute("error", "No se pudo actualizar la farmacia.");
            return "administracion/farmacias";
        }
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable long id, RedirectAttributes redirectAttributes) {
        service.desactivar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Farmacia desactivada. No se elimino fisicamente por auditoria.");
        return "redirect:/farmacias";
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable long id, RedirectAttributes redirectAttributes) {
        service.activar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Farmacia activada correctamente");
        return "redirect:/farmacias";
    }

    private void cargarModeloBase(Model model) {
        model.addAttribute("farmacias", service.listar());
    }
}
