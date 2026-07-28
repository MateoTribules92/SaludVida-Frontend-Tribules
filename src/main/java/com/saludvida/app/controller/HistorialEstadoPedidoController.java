package com.saludvida.app.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.saludvida.app.model.dto.response.HistorialEstadoPedidoResponseDTO;
import com.saludvida.app.model.dto.response.PedidoResponseDTO;
import com.saludvida.app.model.dto.response.UsuarioResponseDTO;
import com.saludvida.app.model.enums.EstadoPedido;
import com.saludvida.app.services.IHistorialEstadoPedidoService;
import com.saludvida.app.services.IPedidoService;
import com.saludvida.app.services.IUsuarioService;

@Controller
@RequestMapping("/historial-estados")
public class HistorialEstadoPedidoController {

    private final IHistorialEstadoPedidoService service;
    private final IPedidoService pedidoService;
    private final IUsuarioService usuarioService;

    public HistorialEstadoPedidoController(
            IHistorialEstadoPedidoService service,
            IPedidoService pedidoService,
            IUsuarioService usuarioService) {
        this.service = service;
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) Long pedido,
            @RequestParam(required = false) Long usuario,
            @RequestParam(required = false) EstadoPedido estado,
            Model model) {

        List<HistorialEstadoPedidoResponseDTO> historial = cargarHistorial(pedido, usuario).stream()
                .filter(h -> estado == null || h.getEstadoNuevo() == estado)
                .sorted(Comparator.comparing(
                        HistorialEstadoPedidoResponseDTO::getFechaRegistro,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        List<PedidoResponseDTO> pedidos = pedidoService.listar();
        List<UsuarioResponseDTO> usuarios = usuarioService.listar();

        Map<Long, String> numerosPedidos = pedidos.stream()
                .collect(Collectors.toMap(
                        PedidoResponseDTO::getIdPedido,
                        PedidoResponseDTO::getNumeroPedido,
                        (actual, repetido) -> actual));

        Map<Long, EstadoPedido> estadosActualesPedidos = pedidos.stream()
                .collect(Collectors.toMap(
                        PedidoResponseDTO::getIdPedido,
                        PedidoResponseDTO::getEstado,
                        (actual, repetido) -> actual));

        Map<Long, String> nombresUsuarios = usuarios.stream()
                .collect(Collectors.toMap(
                        UsuarioResponseDTO::getIdUsuario,
                        UsuarioResponseDTO::getNombres,
                        (actual, repetido) -> actual));

        long cambiosAConfirmado = historial.stream()
                .filter(h -> h.getEstadoNuevo() == EstadoPedido.CONFIRMADO)
                .count();
        long cambiosAEnRuta = historial.stream()
                .filter(h -> h.getEstadoNuevo() == EstadoPedido.EN_RUTA)
                .count();
        long cambiosFinales = historial.stream()
                .filter(h -> h.getEstadoNuevo() == EstadoPedido.ENTREGADO
                        || h.getEstadoNuevo() == EstadoPedido.CANCELADO)
                .count();

        model.addAttribute("historial", historial);
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("estadosPedido", EstadoPedido.values());
        model.addAttribute("numerosPedidos", numerosPedidos);
        model.addAttribute("estadosActualesPedidos", estadosActualesPedidos);
        model.addAttribute("nombresUsuarios", nombresUsuarios);
        model.addAttribute("pedidoSeleccionado", pedido);
        model.addAttribute("usuarioSeleccionado", usuario);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("totalCambios", historial.size());
        model.addAttribute("cambiosAConfirmado", cambiosAConfirmado);
        model.addAttribute("cambiosAEnRuta", cambiosAEnRuta);
        model.addAttribute("cambiosFinales", cambiosFinales);

        return "ventas-pedidos/historialestados";
    }

    private List<HistorialEstadoPedidoResponseDTO> cargarHistorial(Long pedido, Long usuario) {
        if (pedido != null && pedido > 0) {
            return service.buscarPorPedido(pedido);
        }

        if (usuario != null && usuario > 0) {
            return service.buscarPorUsuario(usuario);
        }

        return service.listar();
    }
}
