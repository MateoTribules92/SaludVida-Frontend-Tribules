package com.saludvida.app.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.saludvida.app.model.dto.request.RutaRequestDTO;
import com.saludvida.app.model.dto.response.RolResponseDTO;
import com.saludvida.app.model.dto.response.RutaResponseDTO;
import com.saludvida.app.model.dto.response.UsuarioResponseDTO;
import com.saludvida.app.model.dto.response.VehiculoResponseDTO;
import com.saludvida.app.model.enums.EstadoRuta;
import com.saludvida.app.model.enums.EstadoVehiculo;
import com.saludvida.app.services.IRolService;
import com.saludvida.app.services.IRutaService;
import com.saludvida.app.services.IUsuarioService;
import com.saludvida.app.services.IVehiculoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/rutas")
public class RutaController {

    private static final List<String> ZONAS = List.of("Norte", "Centro", "Sur", "Valle");

    private final IRutaService service;
    private final IVehiculoService vehiculoService;
    private final IUsuarioService usuarioService;
    private final IRolService rolService;

    public RutaController(
            IRutaService service,
            IVehiculoService vehiculoService,
            IUsuarioService usuarioService,
            IRolService rolService) {
        this.service = service;
        this.vehiculoService = vehiculoService;
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    @GetMapping
    public String listar(Model model) {
        cargarModeloBase(model);

        if (!model.containsAttribute("request")) {
            RutaRequestDTO request = nuevoRequest();
            model.addAttribute("request", request);
        }

        if (!model.containsAttribute("editando")) {
            model.addAttribute("editando", false);
        }

        return "distribucion/rutas";
    }

    @PostMapping
    public String crear(
            @Valid @ModelAttribute("request") RutaRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        normalizar(request);

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            return "distribucion/rutas";
        }

        try {
            request.setCodigoRuta(null);
            request.setEstado(EstadoRuta.PLANIFICADA);
            service.crear(request);
            redirectAttributes.addFlashAttribute("mensaje", "Ruta creada correctamente.");
            return "redirect:/rutas";
        } catch (Exception e) {
            if (request.getFechaRuta() == null) {
                request.setFechaRuta(LocalDate.now());
            }
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            model.addAttribute("error", "No se pudo crear la ruta. " + obtenerDetalleError(e));
            return "distribucion/rutas";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable long id, Model model, RedirectAttributes redirectAttributes) {
        var ruta = service.buscarPorId(id);

        if (ruta.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Ruta no encontrada.");
            return "redirect:/rutas";
        }

        RutaResponseDTO response = ruta.get();

        if (esEstadoFinal(response.getEstado())) {
            redirectAttributes.addFlashAttribute("error", "No se puede editar una ruta finalizada o cancelada.");
            return "redirect:/rutas";
        }

        cargarModeloBase(model);
        model.addAttribute("request", toRequest(response));
        model.addAttribute("editando", true);
        model.addAttribute("editId", id);
        return "distribucion/rutas";
    }

    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable long id,
            @Valid @ModelAttribute("request") RutaRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        normalizar(request);

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            return "distribucion/rutas";
        }

        try {
            service.actualizar(id, request);
            redirectAttributes.addFlashAttribute("mensaje", "Ruta actualizada correctamente.");
            return "redirect:/rutas";
        } catch (Exception e) {
            if (request.getFechaRuta() == null) {
                request.setFechaRuta(LocalDate.now());
            }
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            model.addAttribute("error", "No se pudo actualizar la ruta. " + obtenerDetalleError(e));
            return "distribucion/rutas";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(
            @PathVariable long id,
            @RequestParam EstadoRuta estado,
            RedirectAttributes redirectAttributes) {

        try {
            service.cambiarEstado(id, estado);
            redirectAttributes.addFlashAttribute("mensaje", "Estado de la ruta actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo cambiar el estado de la ruta. " + obtenerDetalleError(e));
        }

        return "redirect:/rutas";
    }

    private void cargarModeloBase(Model model) {
        List<RutaResponseDTO> rutas = service.listar();
        List<VehiculoResponseDTO> todosVehiculos = vehiculoService.listar();
        List<UsuarioResponseDTO> todosUsuarios = usuarioService.listar();
        List<RolResponseDTO> todosRoles = rolService.listar();

        Map<Long, String> nombresVehiculos = todosVehiculos.stream()
                .collect(Collectors.toMap(
                        VehiculoResponseDTO::getIdVehiculo,
                        v -> v.getPlaca() + " - " + valor(v.getDescripcion()),
                        (actual, repetido) -> actual));

        Map<Long, String> nombresDistribuidores = todosUsuarios.stream()
                .collect(Collectors.toMap(
                        UsuarioResponseDTO::getIdUsuario,
                        UsuarioResponseDTO::getNombres,
                        (actual, repetido) -> actual));

        Map<Long, String> codigosRoles = todosRoles.stream()
                .collect(Collectors.toMap(
                        RolResponseDTO::getIdRol,
                        r -> r.getCodigo() == null ? "" : r.getCodigo().trim().toUpperCase(),
                        (actual, repetido) -> actual));

        List<VehiculoResponseDTO> vehiculosDisponibles = todosVehiculos.stream()
                .filter(v -> Boolean.TRUE.equals(v.getActivo()))
                .filter(v -> v.getEstado() == EstadoVehiculo.DISPONIBLE)
                .toList();

        List<UsuarioResponseDTO> distribuidores = todosUsuarios.stream()
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .filter(u -> {
                    String codigoRol = codigosRoles.getOrDefault(u.getIdRol(), "");
                    return codigoRol.contains("PERSONAL_DISTRIBUCION")
                            || codigoRol.contains("DISTRIBUIDOR")
                            || codigoRol.contains("ADMIN");
                })
                .toList();

        long planificadas = rutas.stream()
                .filter(r -> r.getEstado() == EstadoRuta.PLANIFICADA)
                .count();
        long enRuta = rutas.stream()
                .filter(r -> r.getEstado() == EstadoRuta.EN_RUTA)
                .count();
        long cerradas = rutas.stream()
                .filter(r -> r.getEstado() == EstadoRuta.FINALIZADA || r.getEstado() == EstadoRuta.CANCELADA)
                .count();

        model.addAttribute("rutas", rutas);
        model.addAttribute("vehiculos", vehiculosDisponibles);
        model.addAttribute("distribuidores", distribuidores);
        model.addAttribute("nombresVehiculos", nombresVehiculos);
        model.addAttribute("nombresDistribuidores", nombresDistribuidores);
        model.addAttribute("estados", EstadoRuta.values());
        model.addAttribute("zonas", ZONAS);
        model.addAttribute("rutasPlanificadas", planificadas);
        model.addAttribute("rutasEnRuta", enRuta);
        model.addAttribute("rutasCerradas", cerradas);
        model.addAttribute("totalRutas", rutas.size());
    }

    private RutaRequestDTO nuevoRequest() {
        RutaRequestDTO request = new RutaRequestDTO();
        request.setCodigoRuta(generarSiguienteCodigoRuta());
        request.setFechaRuta(LocalDate.now());
        request.setEstado(EstadoRuta.PLANIFICADA);
        return request;
    }

    private RutaRequestDTO toRequest(RutaResponseDTO response) {
        RutaRequestDTO request = new RutaRequestDTO();
        request.setCodigoRuta(response.getCodigoRuta());
        request.setFechaRuta(response.getFechaRuta());
        request.setZona(response.getZona());
        request.setIdVehiculo(response.getIdVehiculo());
        request.setIdDistribuidor(response.getIdDistribuidor());
        request.setEstado(response.getEstado());
        request.setObservacion(response.getObservacion());
        return request;
    }

    private void normalizar(RutaRequestDTO request) {
        if (request.getCodigoRuta() != null) {
            request.setCodigoRuta(request.getCodigoRuta().trim().toUpperCase());
        }
        if (request.getZona() != null) {
            request.setZona(request.getZona().trim());
        }
        if (request.getObservacion() != null) {
            request.setObservacion(request.getObservacion().trim());
        }
        if (request.getEstado() == null) {
            request.setEstado(EstadoRuta.PLANIFICADA);
        }
    }

    private String generarSiguienteCodigoRuta() {
        int ultimo = service.listar().stream()
                .map(RutaResponseDTO::getCodigoRuta)
                .filter(codigo -> codigo != null && codigo.matches("^RUTA-[0-9]+$"))
                .map(codigo -> codigo.substring(5))
                .mapToInt(numero -> {
                    try {
                        return Integer.parseInt(numero);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max()
                .orElse(0);

        return "RUTA-" + String.format("%04d", ultimo + 1);
    }

    private List<EstadoRuta> estadosPermitidos(EstadoRuta estado) {
        if (estado == EstadoRuta.PLANIFICADA) {
            return List.of(EstadoRuta.EN_RUTA, EstadoRuta.CANCELADA);
        }
        if (estado == EstadoRuta.EN_RUTA) {
            return List.of(EstadoRuta.FINALIZADA, EstadoRuta.CANCELADA);
        }
        return List.of();
    }

    private boolean esEstadoFinal(EstadoRuta estado) {
        return estado == EstadoRuta.FINALIZADA || estado == EstadoRuta.CANCELADA;
    }

    private String valor(String texto) {
        return texto == null || texto.isBlank() ? "Sin descripción" : texto;
    }

    private String obtenerDetalleError(Exception e) {
        if (e instanceof WebClientResponseException webClientException) {
            String response = webClientException.getResponseBodyAsString();

            if (response != null && !response.isBlank()) {
                String message = extraerCampoJson(response, "message");
                String detail = extraerCampoJson(response, "detail");
                String error = extraerCampoJson(response, "error");

                if (message != null && !message.isBlank()) {
                    return limpiarMensaje(message);
                }

                if (detail != null && !detail.isBlank()) {
                    return limpiarMensaje(detail);
                }

                if (error != null && !error.isBlank()) {
                    return limpiarMensaje(error);
                }

                return limpiarMensaje(response);
            }
        }

        return "Verifica fecha, zona, vehículo y distribuidor.";
    }

    private String extraerCampoJson(String json, String campo) {
        String marcador = "\"" + campo + "\":\"";
        int inicio = json.indexOf(marcador);

        if (inicio < 0) {
            return null;
        }

        inicio += marcador.length();
        StringBuilder valor = new StringBuilder();
        boolean escapado = false;

        for (int i = inicio; i < json.length(); i++) {
            char c = json.charAt(i);

            if (escapado) {
                valor.append(c);
                escapado = false;
                continue;
            }

            if (c == '\\') {
                escapado = true;
                continue;
            }

            if (c == '"') {
                break;
            }

            valor.append(c);
        }

        return valor.toString();
    }

    private String limpiarMensaje(String mensaje) {
        if (mensaje == null || mensaje.isBlank()) {
            return "No se pudo completar la acción por una regla de negocio.";
        }

        if (mensaje.contains("vehículo ya tiene") || mensaje.contains("vehiculo ya tiene")) {
            return "El vehículo seleccionado ya tiene una ruta activa para esa fecha.";
        }

        if (mensaje.contains("distribuidor ya tiene")) {
            return "El distribuidor seleccionado ya tiene una ruta activa para esa fecha.";
        }

        if (mensaje.contains("Debe seleccionar un vehículo")) {
            return "Debes seleccionar un vehículo disponible.";
        }

        if (mensaje.contains("Debe seleccionar un distribuidor")) {
            return "Debes seleccionar un usuario de distribución o administrador activo.";
        }

        if (mensaje.contains("DISTRIBUIDOR") || mensaje.contains("ADMINISTRADOR")) {
            return "El usuario seleccionado debe tener rol PERSONAL_DISTRIBUCION, DISTRIBUIDOR o ADMINISTRADOR.";
        }

        if (mensaje.contains("PERSONAL_DISTRIBUCION")) {
            return "El usuario asignado debe tener rol PERSONAL_DISTRIBUCION activo. Puedes cambiar el código del rol DISTRIBUIDOR a PERSONAL_DISTRIBUCION.";
        }

        if (mensaje.contains("DISPONIBLE")) {
            return "El vehículo debe estar DISPONIBLE para asignarlo a una ruta.";
        }

        if (mensaje.contains("fecha de ruta no puede ser anterior") || mensaje.contains("FutureOrPresent")) {
            return "La fecha de ruta no puede ser anterior a la fecha actual.";
        }

        if (mensaje.contains("fecha de ruta es obligatoria") || mensaje.contains("fechaRuta") || mensaje.contains("LocalDate")) {
            return "La fecha de ruta es obligatoria o tiene un formato inválido.";
        }

        if (mensaje.contains("Cambio de estado")) {
            return "Cambio de estado no permitido para la ruta.";
        }

        return "No se pudo completar la acción por una regla de negocio.";
    }

    @ModelAttribute("estadosPermitidosHelper")
    public Object estadosPermitidosHelper() {
        return new Object() {
            @SuppressWarnings("unused")
            public List<EstadoRuta> para(EstadoRuta estado) {
                return estadosPermitidos(estado);
            }
        };
    }
}
