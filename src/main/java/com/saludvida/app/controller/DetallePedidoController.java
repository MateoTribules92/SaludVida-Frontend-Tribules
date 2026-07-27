package com.saludvida.app.controller;

import java.math.BigDecimal;
import java.util.Optional;
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

import com.saludvida.app.model.dto.request.DetallePedidoRequestDTO;
import com.saludvida.app.model.dto.response.PedidoResponseDTO;
import com.saludvida.app.model.dto.response.ProductoResponseDTO;
import com.saludvida.app.services.IDetallePedidoService;
import com.saludvida.app.services.IPedidoService;
import com.saludvida.app.services.IProductoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/detalles-pedido")
public class DetallePedidoController {

    private final IDetallePedidoService service;
    private final IPedidoService pedidoService;
    private final IProductoService productoService;

    public DetallePedidoController(
            IDetallePedidoService service,
            IPedidoService pedidoService,
            IProductoService productoService) {
        this.service = service;
        this.pedidoService = pedidoService;
        this.productoService = productoService;
    }

    @GetMapping
    public String listar(@RequestParam(name = "pedido", required = false) Long idPedido, Model model) {
        cargarModeloBase(model, idPedido);

        if (!model.containsAttribute("request")) {
            DetallePedidoRequestDTO request = new DetallePedidoRequestDTO();

            if (idPedido != null && idPedido > 0) {
                request.setIdPedido(idPedido);
            }

            model.addAttribute("request", request);
        }

        if (!model.containsAttribute("editando")) {
            model.addAttribute("editando", false);
        }

        return "ventas-pedidos/detallespedido";
    }

    @PostMapping
    public String guardar(
            @Valid @ModelAttribute("request") DetallePedidoRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarModeloBase(model, request.getIdPedido());
            return "ventas-pedidos/detallespedido";
        }

        try {
            request.setSubtotal(calcularSubtotal(request));
            service.guardar(request);
            redirectAttributes.addFlashAttribute("mensaje", "Producto agregado al pedido correctamente");
            return "redirect:/detalles-pedido?pedido=" + request.getIdPedido();
        } catch (Exception e) {
            cargarModeloBase(model, request.getIdPedido());
            model.addAttribute("error", "No se pudo agregar el producto. Verifica stock disponible, pedido y producto seleccionado.");
            return "ventas-pedidos/detallespedido";
        }
    }

    @GetMapping("/{id}/editar")
    public String mostrarEditar(
            @PathVariable long id,
            @RequestParam(name = "pedido", required = false) Long idPedido,
            Model model,
            RedirectAttributes redirectAttributes) {

        var detalle = service.buscarPorId(id);

        if (detalle.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Detalle de pedido no encontrado");
            return idPedido != null ? "redirect:/detalles-pedido?pedido=" + idPedido : "redirect:/detalles-pedido";
        }

        var response = detalle.get();
        DetallePedidoRequestDTO request = new DetallePedidoRequestDTO();
        request.setIdPedido(response.getIdPedido());
        request.setIdProducto(response.getIdProducto());
        request.setCantidad(response.getCantidad());
        request.setPrecioUnitario(response.getPrecioUnitario());
        request.setSubtotal(response.getSubtotal());

        cargarModeloBase(model, response.getIdPedido());
        model.addAttribute("request", request);
        model.addAttribute("editando", true);
        model.addAttribute("editId", id);

        return "ventas-pedidos/detallespedido";
    }

    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable long id,
            @Valid @ModelAttribute("request") DetallePedidoRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarModeloBase(model, request.getIdPedido());
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            return "ventas-pedidos/detallespedido";
        }

        try {
            request.setSubtotal(calcularSubtotal(request));
            service.actualizar(id, request);
            redirectAttributes.addFlashAttribute("mensaje", "Producto del pedido actualizado correctamente");
            return "redirect:/detalles-pedido?pedido=" + request.getIdPedido();
        } catch (Exception e) {
            cargarModeloBase(model, request.getIdPedido());
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            model.addAttribute("error", "No se pudo actualizar el producto. Solo se permite modificar detalles de pedidos pendientes y con stock suficiente.");
            return "ventas-pedidos/detallespedido";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(
            @PathVariable long id,
            @RequestParam(name = "pedido", required = false) Long idPedido,
            RedirectAttributes redirectAttributes) {

        try {
            service.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Producto quitado del pedido correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo quitar el producto. Solo se permite modificar detalles de pedidos pendientes.");
        }

        return idPedido != null ? "redirect:/detalles-pedido?pedido=" + idPedido : "redirect:/detalles-pedido";
    }

    private void cargarModeloBase(Model model, Long idPedidoSeleccionado) {
        var pedidos = pedidoService.listar();
        Optional<PedidoResponseDTO> pedidoSeleccionado = Optional.empty();

        if (idPedidoSeleccionado != null && idPedidoSeleccionado > 0) {
            pedidoSeleccionado = pedidos.stream()
                    .filter(p -> p.getIdPedido() == idPedidoSeleccionado)
                    .findFirst();
        }

        var detalles = pedidoSeleccionado
                .map(p -> service.buscarPorPedido(p.getIdPedido()))
                .orElseGet(service::listar);

        var todosProductos = productoService.listar();
        var productosActivos = todosProductos.stream()
                .filter(p -> Boolean.TRUE.equals(p.getActivo()))
                .toList();

        var nombresPedidos = pedidos.stream()
                .collect(Collectors.toMap(
                        PedidoResponseDTO::getIdPedido,
                        p -> p.getNumeroPedido() + " - " + p.getEstado(),
                        (actual, repetido) -> actual));

        var nombresProductos = todosProductos.stream()
                .collect(Collectors.toMap(
                        ProductoResponseDTO::getIdProducto,
                        p -> p.getNombre() + " (" + p.getCodigo() + ")",
                        (actual, repetido) -> actual));

        BigDecimal totalDetalles = detalles.stream()
                .map(d -> d.getSubtotal() == null ? BigDecimal.ZERO : d.getSubtotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long lineas = detalles.size();

        model.addAttribute("detalles", detalles);
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("productos", productosActivos);
        model.addAttribute("nombresPedidos", nombresPedidos);
        model.addAttribute("nombresProductos", nombresProductos);
        model.addAttribute("totalDetalles", totalDetalles);
        model.addAttribute("lineasDetalle", lineas);
        model.addAttribute("pedidoSeleccionado", pedidoSeleccionado.orElse(null));
        model.addAttribute("idPedidoSeleccionado", pedidoSeleccionado.map(PedidoResponseDTO::getIdPedido).orElse(0L));
        model.addAttribute("detalleFiltrado", pedidoSeleccionado.isPresent());
    }

    private BigDecimal calcularSubtotal(DetallePedidoRequestDTO request) {
        if (request.getPrecioUnitario() == null || request.getCantidad() == null) {
            return BigDecimal.ZERO;
        }

        return request.getPrecioUnitario().multiply(BigDecimal.valueOf(request.getCantidad()));
    }
}
