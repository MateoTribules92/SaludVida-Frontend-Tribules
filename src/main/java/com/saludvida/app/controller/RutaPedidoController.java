package com.saludvida.app.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.saludvida.app.model.dto.request.RutaPedidoRequestDTO;
import com.saludvida.app.model.dto.response.ClienteResponseDTO;
import com.saludvida.app.model.dto.response.PedidoResponseDTO;
import com.saludvida.app.model.dto.response.RutaPedidoResponseDTO;
import com.saludvida.app.model.dto.response.RutaResponseDTO;
import com.saludvida.app.model.dto.response.VehiculoResponseDTO;
import com.saludvida.app.model.enums.EstadoPedido;
import com.saludvida.app.model.enums.EstadoRuta;
import com.saludvida.app.services.IClienteService;
import com.saludvida.app.services.IPedidoService;
import com.saludvida.app.services.IRutaPedidoService;
import com.saludvida.app.services.IRutaService;
import com.saludvida.app.services.IVehiculoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/ruta-pedidos")
public class RutaPedidoController {

    private final IRutaPedidoService service;
    private final IRutaService rutaService;
    private final IPedidoService pedidoService;
    private final IClienteService clienteService;
    private final IVehiculoService vehiculoService;

    public RutaPedidoController(
            IRutaPedidoService service,
            IRutaService rutaService,
            IPedidoService pedidoService,
            IClienteService clienteService,
            IVehiculoService vehiculoService) {
        this.service = service;
        this.rutaService = rutaService;
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) Long ruta,
            @RequestParam(required = false) Long pedido,
            Model model) {
        cargarModeloBase(model, ruta, pedido);

        if (!model.containsAttribute("request")) {
            RutaPedidoRequestDTO request = nuevoRequest(ruta, pedido);
            Object siguienteOrden = model.asMap().get("siguienteOrden");
            if (siguienteOrden instanceof Integer orden) {
                request.setOrdenEntrega(orden);
            }
            model.addAttribute("request", request);
        }

        return "distribucion/ruta-pedidos";
    }

    @PostMapping
    public String asignar(
            @Valid @ModelAttribute("request") RutaPedidoRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        Long rutaSeleccionada = request.getIdRuta() > 0 ? request.getIdRuta() : null;

        if (result.hasErrors()) {
            cargarModeloBase(model, rutaSeleccionada, request.getIdPedido() > 0 ? request.getIdPedido() : null);
            return "distribucion/ruta-pedidos";
        }

        try {
            service.asignar(request);
            redirectAttributes.addFlashAttribute("mensaje", "Pedido asignado a la ruta correctamente.");
            return "redirect:/ruta-pedidos?ruta=" + request.getIdRuta();
        } catch (Exception e) {
            cargarModeloBase(model, rutaSeleccionada, request.getIdPedido() > 0 ? request.getIdPedido() : null);
            model.addAttribute("error", "No se pudo asignar el pedido. " + obtenerDetalleError(e));
            return "distribucion/ruta-pedidos";
        }
    }

    private void cargarModeloBase(Model model, Long idRutaSeleccionada, Long idPedidoSeleccionado) {
        List<RutaResponseDTO> todasRutas = rutaService.listar();
        List<PedidoResponseDTO> todosPedidos = pedidoService.listar();
        List<RutaPedidoResponseDTO> asignaciones = service.listar();
        List<ClienteResponseDTO> clientes = clienteService.listar();
        List<VehiculoResponseDTO> vehiculos = vehiculoService.listar();

        Map<Long, ClienteResponseDTO> clientesPorId = clientes.stream()
                .collect(Collectors.toMap(
                        ClienteResponseDTO::getIdCliente,
                        c -> c,
                        (actual, repetido) -> actual));

        List<RutaResponseDTO> rutasPlanificadas = todasRutas.stream()
                .filter(r -> r.getEstado() == EstadoRuta.PLANIFICADA)
                .toList();

        Set<Long> pedidosAsignados = asignaciones.stream()
                .map(RutaPedidoResponseDTO::getIdPedido)
                .collect(Collectors.toSet());

        List<PedidoResponseDTO> pedidosElegibles = todosPedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.CONFIRMADO || p.getEstado() == EstadoPedido.EN_PREPARACION)
                .filter(p -> !pedidosAsignados.contains(p.getIdPedido()))
                .toList();

        RutaResponseDTO rutaSeleccionada = idRutaSeleccionada == null ? null : todasRutas.stream()
                .filter(r -> r.getIdRuta() == idRutaSeleccionada)
                .findFirst()
                .orElse(null);

        PedidoResponseDTO pedidoSeleccionado = idPedidoSeleccionado == null ? null : todosPedidos.stream()
                .filter(p -> p.getIdPedido() == idPedidoSeleccionado)
                .findFirst()
                .orElse(null);

        String zonaPedidoSeleccionado = pedidoSeleccionado == null
                ? null
                : clientesPorId.get(pedidoSeleccionado.getIdCliente()) == null
                    ? null
                    : clientesPorId.get(pedidoSeleccionado.getIdCliente()).getZona();

        boolean pedidoSeleccionadoCompatible = pedidoSeleccionado != null
                && rutaSeleccionada != null
                && zonasCoinciden(rutaSeleccionada.getZona(), zonaPedidoSeleccionado);

        if (rutaSeleccionada != null) {
            Map<Long, String> zonasClientes = clientes.stream()
                    .collect(Collectors.toMap(
                            ClienteResponseDTO::getIdCliente,
                            c -> c.getZona() == null ? "" : c.getZona(),
                            (actual, repetido) -> actual));

            pedidosElegibles = pedidosElegibles.stream()
                    .filter(p -> zonasCoinciden(rutaSeleccionada.getZona(), zonasClientes.get(p.getIdCliente())))
                    .toList();
        }

        List<RutaPedidoResponseDTO> asignacionesFiltradas = idRutaSeleccionada == null
                ? asignaciones
                : asignaciones.stream()
                    .filter(rp -> rp.getIdRuta() == idRutaSeleccionada)
                    .toList();

        Map<Long, String> nombresPedidos = todosPedidos.stream()
                .collect(Collectors.toMap(
                        PedidoResponseDTO::getIdPedido,
                        p -> p.getNumeroPedido() + " | " + p.getEstado() + " | $" + p.getTotal(),
                        (actual, repetido) -> actual));

        Map<Long, String> nombresRutas = todasRutas.stream()
                .collect(Collectors.toMap(
                        RutaResponseDTO::getIdRuta,
                        r -> r.getCodigoRuta() + " | " + r.getZona() + " | " + r.getFechaRuta(),
                        (actual, repetido) -> actual));

        Map<Long, String> nombresClientes = clientes.stream()
                .collect(Collectors.toMap(
                        ClienteResponseDTO::getIdCliente,
                        c -> c.getNombres() + " | Zona: " + valor(c.getZona()),
                        (actual, repetido) -> actual));

        Map<Long, String> zonasPedidos = todosPedidos.stream()
                .collect(Collectors.toMap(
                        PedidoResponseDTO::getIdPedido,
                        p -> clientesPorId.get(p.getIdCliente()) == null
                                ? "Sin zona"
                                : valor(clientesPorId.get(p.getIdCliente()).getZona()),
                        (actual, repetido) -> actual));

        Map<Long, String> nombresVehiculos = vehiculos.stream()
                .collect(Collectors.toMap(
                        VehiculoResponseDTO::getIdVehiculo,
                        v -> v.getPlaca() + " | Cap. " + v.getCapacidadPedidos() + " pedidos",
                        (actual, repetido) -> actual));

        int siguienteOrden = asignacionesFiltradas.stream()
                .map(RutaPedidoResponseDTO::getOrdenEntrega)
                .filter(o -> o != null)
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0) + 1;

        model.addAttribute("rutas", rutasPlanificadas);
        model.addAttribute("rutaSeleccionada", rutaSeleccionada);
        model.addAttribute("pedidoSeleccionado", pedidoSeleccionado);
        model.addAttribute("zonaPedidoSeleccionado", zonaPedidoSeleccionado);
        model.addAttribute("pedidoSeleccionadoCompatible", pedidoSeleccionadoCompatible);
        model.addAttribute("pedidos", pedidosElegibles);
        model.addAttribute("asignaciones", asignacionesFiltradas);
        model.addAttribute("nombresPedidos", nombresPedidos);
        model.addAttribute("nombresRutas", nombresRutas);
        model.addAttribute("nombresClientes", nombresClientes);
        model.addAttribute("zonasPedidos", zonasPedidos);
        model.addAttribute("nombresVehiculos", nombresVehiculos);
        model.addAttribute("todosPedidos", todosPedidos);
        model.addAttribute("totalAsignaciones", asignacionesFiltradas.size());
        model.addAttribute("pedidosElegibles", pedidosElegibles.size());
        model.addAttribute("siguienteOrden", siguienteOrden);
    }

    private RutaPedidoRequestDTO nuevoRequest(Long ruta, Long pedido) {
        RutaPedidoRequestDTO request = new RutaPedidoRequestDTO();
        request.setIdRuta(ruta == null ? 0L : ruta);
        request.setIdPedido(pedido == null ? 0L : pedido);
        request.setOrdenEntrega(1);
        return request;
    }

    private boolean zonasCoinciden(String zonaRuta, String zonaCliente) {
        if (zonaRuta == null || zonaCliente == null) {
            return false;
        }
        return zonaRuta.trim().equalsIgnoreCase(zonaCliente.trim());
    }

    private String valor(String texto) {
        return texto == null || texto.isBlank() ? "Sin dato" : texto;
    }

    private String obtenerDetalleError(Exception e) {
        if (e instanceof WebClientResponseException webClientException) {
            String response = webClientException.getResponseBodyAsString();
            if (response != null && !response.isBlank()) {
                String message = extraerCampoJson(response, "message");
                String detail = extraerCampoJson(response, "detail");

                if (message != null && !message.isBlank()) {
                    return limpiarMensaje(message);
                }

                if (detail != null && !detail.isBlank()) {
                    return limpiarMensaje(detail);
                }

                return limpiarMensaje(response);
            }
        }
        return "Verifica ruta, pedido, zona y capacidad del vehículo.";
    }

    private String limpiarMensaje(String mensaje) {
        if (mensaje == null || mensaje.isBlank()) {
            return "No se pudo completar la acción por una regla de negocio.";
        }

        if (mensaje.contains("CONFIRMADOS") || mensaje.contains("EN_PREPARACION")) {
            return "Solo se pueden asignar pedidos CONFIRMADOS o EN_PREPARACION.";
        }

        if (mensaje.contains("zona del cliente")) {
            return "La zona del cliente no coincide con la zona de la ruta.";
        }

        if (mensaje.contains("capacidad")) {
            return "La ruta ya alcanzó la capacidad máxima de pedidos del vehículo.";
        }

        if (mensaje.contains("PLANIFICADA")) {
            return "La ruta debe estar PLANIFICADA para asignar pedidos.";
        }

        if (mensaje.contains("ya se encuentra asignado") || mensaje.contains("uq_pedido_en_ruta")
                || mensaje.contains("duplicate")) {
            return "El pedido ya se encuentra asignado a una ruta.";
        }

        if (mensaje.contains("orden")) {
            return "Ya existe un pedido con ese orden de entrega en la ruta.";
        }

        return "No se pudo completar la acción por una regla de negocio.";
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
}
