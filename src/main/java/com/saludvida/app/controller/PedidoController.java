package com.saludvida.app.controller;

import java.math.BigDecimal;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.saludvida.app.model.dto.request.PedidoRequestDTO;
import com.saludvida.app.model.dto.response.ClienteResponseDTO;
import com.saludvida.app.model.dto.response.FarmaciaResponseDTO;
import com.saludvida.app.model.dto.response.PedidoResponseDTO;
import com.saludvida.app.model.dto.response.UsuarioResponseDTO;
import com.saludvida.app.model.enums.EstadoPedido;
import com.saludvida.app.services.IClienteService;
import com.saludvida.app.services.IFarmaciaService;
import com.saludvida.app.services.IPedidoService;
import com.saludvida.app.services.IRolService;
import com.saludvida.app.services.IUsuarioService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final IPedidoService service;
    private final IClienteService clienteService;
    private final IFarmaciaService farmaciaService;
    private final IUsuarioService usuarioService;
    private final IRolService rolService;

    public PedidoController(
            IPedidoService service,
            IClienteService clienteService,
            IFarmaciaService farmaciaService,
            IUsuarioService usuarioService,
            IRolService rolService) {
        this.service = service;
        this.clienteService = clienteService;
        this.farmaciaService = farmaciaService;
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    @GetMapping
    public String listar(Model model) {
        cargarModeloBase(model);

        if (!model.containsAttribute("request")) {
            PedidoRequestDTO request = new PedidoRequestDTO();
            request.setNumeroPedido(generarSiguienteNumeroPedido());
            model.addAttribute("request", request);
        }

        if (!model.containsAttribute("editando")) {
            model.addAttribute("editando", false);
        }

        return "ventas-pedidos/pedidos";
    }

    @PostMapping
    public String crear(
            @Valid @ModelAttribute("request") PedidoRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            return "ventas-pedidos/pedidos";
        }

        try {
            request.setNumeroPedido(null);
            request.setEstado(EstadoPedido.PENDIENTE);
            request.setTotal(BigDecimal.ZERO);

            service.crear(request);
            redirectAttributes.addFlashAttribute("mensaje", "Pedido creado correctamente");
            return "redirect:/pedidos";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            model.addAttribute("error", "No se pudo crear el pedido. " + obtenerDetalleError(e));
            return "ventas-pedidos/pedidos";
        }
    }

    @GetMapping("/{id}/editar")
    public String mostrarEditar(@PathVariable long id, Model model, RedirectAttributes redirectAttributes) {
        var pedido = service.buscarPorId(id);

        if (pedido.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Pedido no encontrado");
            return "redirect:/pedidos";
        }

        PedidoResponseDTO response = pedido.get();
        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setNumeroPedido(response.getNumeroPedido());
        request.setIdCliente(response.getIdCliente());
        request.setIdFarmacia(response.getIdFarmacia());
        request.setIdVendedor(response.getIdVendedor());
        request.setEstado(response.getEstado());
        request.setTotal(response.getTotal());
        request.setDireccionEntrega(response.getDireccionEntrega());
        request.setObservacion(response.getObservacion());

        cargarModeloBase(model);
        model.addAttribute("request", request);
        model.addAttribute("editando", true);
        model.addAttribute("editId", id);

        return "ventas-pedidos/pedidos";
    }

    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable long id,
            @Valid @ModelAttribute("request") PedidoRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            return "ventas-pedidos/pedidos";
        }

        try {
            var pedidoActual = service.buscarPorId(id);
            request.setTotal(pedidoActual
                    .map(PedidoResponseDTO::getTotal)
                    .orElse(BigDecimal.ZERO));

            service.actualizar(id, request);
            redirectAttributes.addFlashAttribute("mensaje", "Pedido actualizado correctamente");
            return "redirect:/pedidos";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            model.addAttribute("error", "No se pudo actualizar el pedido. " + obtenerDetalleError(e));
            return "ventas-pedidos/pedidos";
        }
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(
            @PathVariable long id,
            @RequestParam EstadoPedido estado,
            RedirectAttributes redirectAttributes) {

        try {
            service.cambiarEstado(id, estado);
            redirectAttributes.addFlashAttribute("mensaje", "Estado del pedido actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo cambiar el estado del pedido. " + obtenerDetalleError(e));
        }

        return "redirect:/pedidos";
    }

    private void cargarModeloBase(Model model) {
        var pedidos = service.listar();
        var todosClientes = clienteService.listar();
        var todasFarmacias = farmaciaService.listar();
        var todosVendedores = usuarioService.listar();
        var todosRoles = rolService.listar();

        var clientes = todosClientes.stream()
                .filter(c -> Boolean.TRUE.equals(c.getActivo()))
                .toList();
        var farmacias = todasFarmacias.stream()
                .filter(f -> Boolean.TRUE.equals(f.getActivo()))
                .toList();
        var nombresClientes = todosClientes.stream()
                .collect(Collectors.toMap(
                        ClienteResponseDTO::getIdCliente,
                        ClienteResponseDTO::getNombres,
                        (actual, repetido) -> actual));

        var nombresFarmacias = todasFarmacias.stream()
                .collect(Collectors.toMap(
                        FarmaciaResponseDTO::getIdFarmacia,
                        FarmaciaResponseDTO::getNombre,
                        (actual, repetido) -> actual));

        var nombresVendedores = todosVendedores.stream()
                .collect(Collectors.toMap(
                        UsuarioResponseDTO::getIdUsuario,
                        UsuarioResponseDTO::getNombres,
                        (actual, repetido) -> actual));

        var codigosRoles = todosRoles.stream()
                .collect(Collectors.toMap(
                        r -> r.getIdRol(),
                        r -> r.getCodigo(),
                        (actual, repetido) -> actual));

        var vendedores = todosVendedores.stream()
                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                .filter(u -> {
                    String codigoRol = codigosRoles.get(u.getIdRol());
                    return "ADMINISTRADOR".equals(codigoRol) || "VENDEDOR".equals(codigoRol);
                })
                .toList();

        long pendientes = pedidos.stream()
                .filter(p -> EstadoPedido.PENDIENTE.equals(p.getEstado()))
                .count();

        long confirmados = pedidos.stream()
                .filter(p -> EstadoPedido.CONFIRMADO.equals(p.getEstado())
                        || EstadoPedido.EN_PREPARACION.equals(p.getEstado())
                        || EstadoPedido.EN_RUTA.equals(p.getEstado()))
                .count();

        long cerrados = pedidos.stream()
                .filter(p -> EstadoPedido.ENTREGADO.equals(p.getEstado())
                        || EstadoPedido.CANCELADO.equals(p.getEstado()))
                .count();

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("clientes", clientes);
        model.addAttribute("farmacias", farmacias);
        model.addAttribute("vendedores", vendedores);
        model.addAttribute("estados", EstadoPedido.values());
        model.addAttribute("nombresClientes", nombresClientes);
        model.addAttribute("nombresFarmacias", nombresFarmacias);
        model.addAttribute("nombresVendedores", nombresVendedores);
        model.addAttribute("pedidosPendientes", pendientes);
        model.addAttribute("pedidosEnProceso", confirmados);
        model.addAttribute("pedidosCerrados", cerrados);
    }

    private String generarSiguienteNumeroPedido() {
        int ultimo = service.listar().stream()
                .map(PedidoResponseDTO::getNumeroPedido)
                .filter(numero -> numero != null && numero.matches("^PED-[0-9]+$"))
                .map(numero -> numero.substring(4))
                .mapToInt(numero -> {
                    try {
                        return Integer.parseInt(numero);
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max()
                .orElse(0);

        return "PED-" + String.format("%04d", ultimo + 1);
    }

    private String obtenerDetalleError(Exception e) {
        if (e instanceof WebClientResponseException webClientException) {
            String response = webClientException.getResponseBodyAsString();

            if (response != null && !response.isBlank()) {
                String marcador = "\"message\":\"";
                int inicio = response.indexOf(marcador);

                if (inicio >= 0) {
                    inicio += marcador.length();
                    int fin = response.indexOf("\"", inicio);

                    if (fin > inicio) {
                        return limpiarMensajeTecnico(response.substring(inicio, fin));
                    }
                }

                return limpiarMensajeTecnico(response);
            }
        }

        return "Verifica que el número no esté duplicado, que las relaciones existan y que el vendedor tenga rol VENDEDOR o ADMINISTRADOR activo.";
    }

    private String limpiarMensajeTecnico(String mensaje) {
        if (mensaje == null || mensaje.isBlank()) {
            return "Ocurrió un error al procesar la solicitud.";
        }

        if (mensaje.contains("sin detalle de productos")) {
            return "Antes de confirmar el pedido debes agregar al menos un producto en el detalle.";
        }

        if (mensaje.contains("Cambio de estado no permitido")) {
            int inicio = mensaje.indexOf("Cambio de estado no permitido");
            int fin = mensaje.indexOf("\\n", inicio);
            return fin > inicio ? mensaje.substring(inicio, fin) : mensaje.substring(inicio);
        }

        if (mensaje.contains("ruta asignada") || mensaje.contains("asignarlo a una ruta")) {
            return "Para pasar un pedido a EN_RUTA primero debes asignarlo a una ruta.";
        }

        if (mensaje.contains("stock")) {
            return "No se pudo confirmar el pedido porque no hay stock suficiente para uno o más productos.";
        }

        if (mensaje.contains("ADMINISTRADOR o VENDEDOR")) {
            return "El vendedor seleccionado debe tener rol ADMINISTRADOR o VENDEDOR activo.";
        }

        if (mensaje.contains("duplicate")) {
            return "Ya existe un pedido con ese número.";
        }

        return "No se pudo completar la acción por una regla de negocio.";
    }
}
