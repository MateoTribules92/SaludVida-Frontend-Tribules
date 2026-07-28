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

import com.saludvida.app.model.dto.request.VehiculoRequestDTO;
import com.saludvida.app.model.dto.response.VehiculoResponseDTO;
import com.saludvida.app.model.enums.EstadoVehiculo;
import com.saludvida.app.services.IVehiculoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoController {

    private final IVehiculoService service;

    public VehiculoController(IVehiculoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        if (!model.containsAttribute("request")) {
            model.addAttribute("request", nuevoRequest());
        }
        cargarModeloBase(model);
        model.addAttribute("editando", false);
        return "distribucion/vehiculos";
    }

    @PostMapping
    public String crear(
            @Valid @ModelAttribute("request") VehiculoRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        normalizar(request);

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            return "distribucion/vehiculos";
        }

        try {
            request.setActivo(true);
            service.crear(request);
            redirectAttributes.addFlashAttribute("success", "Vehículo registrado correctamente.");
            return "redirect:/vehiculos";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            model.addAttribute("error", obtenerMensajeError(e, "No se pudo registrar el vehículo."));
            return "distribucion/vehiculos";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable long id, Model model, RedirectAttributes redirectAttributes) {
        var vehiculo = service.buscarPorId(id);

        if (vehiculo.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vehículo no encontrado.");
            return "redirect:/vehiculos";
        }

        model.addAttribute("request", toRequest(vehiculo.get()));
        model.addAttribute("editando", true);
        model.addAttribute("editId", id);
        cargarModeloBase(model);
        return "distribucion/vehiculos";
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(
            @PathVariable long id,
            @Valid @ModelAttribute("request") VehiculoRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        normalizar(request);

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            return "distribucion/vehiculos";
        }

        try {
            service.actualizar(id, request);
            redirectAttributes.addFlashAttribute("success", "Vehículo actualizado correctamente.");
            return "redirect:/vehiculos";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            model.addAttribute("error", obtenerMensajeError(e, "No se pudo actualizar el vehículo."));
            return "distribucion/vehiculos";
        }
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable long id, RedirectAttributes redirectAttributes) {
        try {
            service.desactivar(id);
            redirectAttributes.addFlashAttribute("success", "Vehículo desactivado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", obtenerMensajeError(e, "No se pudo desactivar el vehículo."));
        }
        return "redirect:/vehiculos";
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable long id, RedirectAttributes redirectAttributes) {
        try {
            service.activar(id);
            redirectAttributes.addFlashAttribute("success", "Vehículo activado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", obtenerMensajeError(e, "No se pudo activar el vehículo."));
        }
        return "redirect:/vehiculos";
    }

    private void cargarModeloBase(Model model) {
        List<VehiculoResponseDTO> vehiculos = service.listar();
        model.addAttribute("vehiculos", vehiculos);
        model.addAttribute("estados", EstadoVehiculo.values());
        model.addAttribute("totalVehiculos", vehiculos.size());
        model.addAttribute("vehiculosActivos", vehiculos.stream().filter(v -> Boolean.TRUE.equals(v.getActivo())).count());
        model.addAttribute("vehiculosDisponibles", vehiculos.stream()
                .filter(v -> Boolean.TRUE.equals(v.getActivo()))
                .filter(v -> v.getEstado() == EstadoVehiculo.DISPONIBLE)
                .count());
        model.addAttribute("vehiculosMantenimiento", vehiculos.stream()
                .filter(v -> v.getEstado() == EstadoVehiculo.MANTENIMIENTO)
                .count());
    }

    private VehiculoRequestDTO nuevoRequest() {
        VehiculoRequestDTO request = new VehiculoRequestDTO();
        request.setEstado(EstadoVehiculo.DISPONIBLE);
        request.setActivo(true);
        return request;
    }

    private VehiculoRequestDTO toRequest(VehiculoResponseDTO vehiculo) {
        VehiculoRequestDTO request = new VehiculoRequestDTO();
        request.setPlaca(vehiculo.getPlaca());
        request.setDescripcion(vehiculo.getDescripcion());
        request.setCapacidadPedidos(vehiculo.getCapacidadPedidos());
        request.setCapacidadKg(vehiculo.getCapacidadKg());
        request.setEstado(vehiculo.getEstado());
        request.setActivo(vehiculo.getActivo());
        return request;
    }

    private void normalizar(VehiculoRequestDTO request) {
        if (request.getPlaca() != null) {
            request.setPlaca(request.getPlaca().trim().toUpperCase());
        }
        if (request.getDescripcion() != null) {
            request.setDescripcion(request.getDescripcion().trim());
        }
        if (request.getEstado() == null) {
            request.setEstado(EstadoVehiculo.DISPONIBLE);
        }
        if (request.getActivo() == null) {
            request.setActivo(true);
        }
    }

    private String obtenerMensajeError(Exception e, String mensajeDefault) {
        String mensaje = e.getMessage();
        if (mensaje == null || mensaje.isBlank()) {
            return mensajeDefault;
        }
        if (mensaje.contains("Ya existe")) {
            return mensaje;
        }
        if (mensaje.contains("400 Bad Request")) {
            return "No se pudo guardar el vehículo. Verifica que la placa no esté duplicada y que las capacidades sean válidas.";
        }
        return mensajeDefault;
    }
}
