package com.saludvida.app.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.saludvida.app.model.dto.response.ClienteResponseDTO;
import com.saludvida.app.model.dto.response.FarmaciaResponseDTO;
import com.saludvida.app.model.dto.response.InventarioResponseDTO;
import com.saludvida.app.model.dto.response.PedidoResponseDTO;
import com.saludvida.app.model.dto.response.ProductoResponseDTO;
import com.saludvida.app.model.enums.EstadoPedido;
import com.saludvida.app.model.enums.EstadoRuta;
import com.saludvida.app.model.enums.TipoMovimiento;
import com.saludvida.app.services.IClienteService;
import com.saludvida.app.services.IFarmaciaService;
import com.saludvida.app.services.IInventarioService;
import com.saludvida.app.services.IMovimientoInventarioService;
import com.saludvida.app.services.IPedidoService;
import com.saludvida.app.services.IProductoService;
import com.saludvida.app.services.IRutaService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final IPedidoService pedidoService;
    private final IInventarioService inventarioService;
    private final IProductoService productoService;
    private final IFarmaciaService farmaciaService;
    private final IClienteService clienteService;
    private final IRutaService rutaService;
    private final IMovimientoInventarioService movimientoService;

    public ReporteController(
            IPedidoService pedidoService,
            IInventarioService inventarioService,
            IProductoService productoService,
            IFarmaciaService farmaciaService,
            IClienteService clienteService,
            IRutaService rutaService,
            IMovimientoInventarioService movimientoService) {
        this.pedidoService = pedidoService;
        this.inventarioService = inventarioService;
        this.productoService = productoService;
        this.farmaciaService = farmaciaService;
        this.clienteService = clienteService;
        this.rutaService = rutaService;
        this.movimientoService = movimientoService;
    }

    @GetMapping
    public String ver(Model model, HttpSession session) {
        var pedidos = pedidoService.listar();
        var inventarios = inventarioService.listar();
        var productos = productoService.listar();
        var farmacias = farmaciaService.listar();
        var clientes = clienteService.listar();
        var rutas = rutaService.listar();
        var movimientos = movimientoService.listar();

        Long idFarmaciaSesion = obtenerIdFarmaciaSesion(session);
        boolean esAdmin = esAdmin(session);

        if (!esAdmin && idFarmaciaSesion != null) {
            inventarios = inventarios.stream()
                    .filter(i -> i.getIdFarmacia() == idFarmaciaSesion)
                    .toList();

            Set<Long> idsInventarioPermitidos = inventarios.stream()
                    .map(InventarioResponseDTO::getIdInventario)
                    .collect(Collectors.toSet());

            movimientos = movimientos.stream()
                    .filter(m -> idsInventarioPermitidos.contains(m.getIdInventario()))
                    .toList();

            pedidos = pedidos.stream()
                    .filter(p -> p.getIdFarmacia() == idFarmaciaSesion)
                    .toList();

            farmacias = farmacias.stream()
                    .filter(f -> f.getIdFarmacia() == idFarmaciaSesion)
                    .toList();
        }

        BigDecimal totalVentas = pedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.ENTREGADO)
                .map(PedidoResponseDTO::getTotal)
                .filter(total -> total != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pedidosPendientes = pedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.PENDIENTE)
                .count();
        long pedidosEnProceso = pedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.CONFIRMADO
                        || p.getEstado() == EstadoPedido.EN_PREPARACION
                        || p.getEstado() == EstadoPedido.EN_RUTA)
                .count();
        long pedidosPorAtender = pedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.PENDIENTE
                        || p.getEstado() == EstadoPedido.CONFIRMADO
                        || p.getEstado() == EstadoPedido.EN_PREPARACION)
                .count();
        long pedidosCerrados = pedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.ENTREGADO
                        || p.getEstado() == EstadoPedido.CANCELADO)
                .count();

        long stockBajo = inventarios.stream()
                .filter(i -> i.getStock() != null
                        && i.getStockMinimo() != null
                        && i.getStock() <= i.getStockMinimo())
                .count();

        LocalDate hoy = LocalDate.now();
        LocalDate limiteCaducidad = hoy.plusDays(30);
        long productosVencidos = productos.stream()
                .filter(p -> p.getFechaCaducidad() != null && p.getFechaCaducidad().isBefore(hoy))
                .count();
        long productosPorCaducar = productos.stream()
                .filter(p -> p.getFechaCaducidad() != null
                        && !p.getFechaCaducidad().isBefore(hoy)
                        && !p.getFechaCaducidad().isAfter(limiteCaducidad))
                .count();

        long rutasPlanificadas = rutas.stream()
                .filter(r -> r.getEstado() == EstadoRuta.PLANIFICADA)
                .count();
        long rutasEnRuta = rutas.stream()
                .filter(r -> r.getEstado() == EstadoRuta.EN_RUTA)
                .count();
        long rutasCerradas = rutas.stream()
                .filter(r -> r.getEstado() == EstadoRuta.FINALIZADA || r.getEstado() == EstadoRuta.CANCELADA)
                .count();

        long entradas = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == TipoMovimiento.ENTRADA)
                .count();
        long salidas = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == TipoMovimiento.SALIDA)
                .count();
        long ajustes = movimientos.stream()
                .filter(m -> m.getTipoMovimiento() == TipoMovimiento.AJUSTE)
                .count();

        Map<Long, String> nombresProductos = productos.stream()
                .collect(Collectors.toMap(
                        ProductoResponseDTO::getIdProducto,
                        ProductoResponseDTO::getNombre,
                        (actual, repetido) -> actual));

        Map<Long, String> nombresFarmacias = farmacias.stream()
                .collect(Collectors.toMap(
                        FarmaciaResponseDTO::getIdFarmacia,
                        FarmaciaResponseDTO::getNombre,
                        (actual, repetido) -> actual));

        Map<Long, String> nombresClientes = clientes.stream()
                .collect(Collectors.toMap(
                        ClienteResponseDTO::getIdCliente,
                        ClienteResponseDTO::getNombres,
                        (actual, repetido) -> actual));

        List<InventarioResponseDTO> inventariosCriticos = inventarios.stream()
                .filter(i -> i.getStock() != null
                        && i.getStockMinimo() != null
                        && i.getStock() <= i.getStockMinimo())
                .sorted(Comparator.comparing(InventarioResponseDTO::getStock, Comparator.nullsLast(Integer::compareTo)))
                .limit(8)
                .toList();

        List<ProductoResponseDTO> productosCaducidad = productos.stream()
                .filter(p -> p.getFechaCaducidad() != null)
                .sorted(Comparator.comparing(ProductoResponseDTO::getFechaCaducidad))
                .limit(8)
                .toList();

        List<PedidoResponseDTO> pedidosRecientes = pedidos.stream()
                .sorted(Comparator.comparing(
                        PedidoResponseDTO::getFechaPedido,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(6)
                .toList();

        model.addAttribute("totalVentas", totalVentas);
        model.addAttribute("totalPedidos", pedidos.size());
        model.addAttribute("totalClientes", clientes.size());
        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("totalFarmacias", farmacias.size());
        model.addAttribute("pedidosPendientes", pedidosPendientes);
        model.addAttribute("pedidosEnProceso", pedidosEnProceso);
        model.addAttribute("pedidosPorAtender", pedidosPorAtender);
        model.addAttribute("pedidosCerrados", pedidosCerrados);
        model.addAttribute("stockBajo", stockBajo);
        model.addAttribute("productosVencidos", productosVencidos);
        model.addAttribute("productosPorCaducar", productosPorCaducar);
        model.addAttribute("rutasPlanificadas", rutasPlanificadas);
        model.addAttribute("rutasEnRuta", rutasEnRuta);
        model.addAttribute("rutasCerradas", rutasCerradas);
        model.addAttribute("entradas", entradas);
        model.addAttribute("salidas", salidas);
        model.addAttribute("ajustes", ajustes);
        model.addAttribute("inventariosCriticos", inventariosCriticos);
        model.addAttribute("productosCaducidad", productosCaducidad);
        model.addAttribute("pedidosRecientes", pedidosRecientes);
        model.addAttribute("nombresProductos", nombresProductos);
        model.addAttribute("nombresFarmacias", nombresFarmacias);
        model.addAttribute("nombresClientes", nombresClientes);

        return "reportes/reporte";
    }

    @GetMapping("/csv")
    public ResponseEntity<String> descargarCsv(HttpSession session) {
        var pedidos = pedidoService.listar();
        var inventarios = inventarioService.listar();
        var productos = productoService.listar();
        var farmacias = farmaciaService.listar();
        var rutas = rutaService.listar();

        Long idFarmaciaSesion = obtenerIdFarmaciaSesion(session);
        boolean esAdmin = esAdmin(session);

        if (!esAdmin && idFarmaciaSesion != null) {
            inventarios = inventarios.stream()
                    .filter(i -> i.getIdFarmacia() == idFarmaciaSesion)
                    .toList();

            pedidos = pedidos.stream()
                    .filter(p -> p.getIdFarmacia() == idFarmaciaSesion)
                    .toList();

            farmacias = farmacias.stream()
                    .filter(f -> f.getIdFarmacia() == idFarmaciaSesion)
                    .toList();
        }

        Map<Long, String> nombresProductos = productos.stream()
                .collect(Collectors.toMap(
                        ProductoResponseDTO::getIdProducto,
                        ProductoResponseDTO::getNombre,
                        (actual, repetido) -> actual));

        Map<Long, String> nombresFarmacias = farmacias.stream()
                .collect(Collectors.toMap(
                        FarmaciaResponseDTO::getIdFarmacia,
                        FarmaciaResponseDTO::getNombre,
                        (actual, repetido) -> actual));

        StringBuilder csv = new StringBuilder();
        csv.append("Reporte SaludVida\n\n");

        csv.append("Pedidos\n");
        csv.append("Numero,Estado,Total,Direccion entrega,Fecha\n");
        pedidos.forEach(p -> csv.append(lineaCsv(
                p.getNumeroPedido(),
                p.getEstado(),
                p.getTotal(),
                p.getDireccionEntrega(),
                p.getFechaPedido())));

        csv.append("\nInventario\n");
        csv.append("Producto,Farmacia,Stock,Stock minimo,Fecha actualizacion\n");
        inventarios.forEach(i -> csv.append(lineaCsv(
                nombresProductos.getOrDefault(i.getIdProducto(), "Producto no encontrado"),
                nombresFarmacias.getOrDefault(i.getIdFarmacia(), "Farmacia no encontrada"),
                i.getStock(),
                i.getStockMinimo(),
                i.getFechaActualizacion())));

        csv.append("\nProductos\n");
        csv.append("Codigo,Nombre,Precio,Fecha caducidad,Activo\n");
        productos.forEach(p -> csv.append(lineaCsv(
                p.getCodigo(),
                p.getNombre(),
                p.getPrecio(),
                p.getFechaCaducidad(),
                p.getActivo())));

        csv.append("\nRutas\n");
        csv.append("Codigo,Zona,Fecha,Estado,Observacion\n");
        rutas.forEach(r -> csv.append(lineaCsv(
                r.getCodigoRuta(),
                r.getZona(),
                r.getFechaRuta(),
                r.getEstado(),
                r.getObservacion())));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=saludvida-reporte.csv")
                .contentType(new MediaType("text", "csv"))
                .body(csv.toString());
    }

    private String lineaCsv(Object... valores) {
        return java.util.Arrays.stream(valores)
                .map(this::csvValor)
                .collect(Collectors.joining(",")) + "\n";
    }

    private String csvValor(Object valor) {
        if (valor == null) {
            return "";
        }

        String texto = valor.toString().replace("\"", "\"\"");
        return "\"" + texto + "\"";
    }

    private boolean esAdmin(HttpSession session) {
        Object rol = session.getAttribute("rolUsuario");
        return "ADMINISTRADOR".equals(rol) || "ADMIN".equals(rol);
    }

    private Long obtenerIdFarmaciaSesion(HttpSession session) {
        Object valor = session.getAttribute("idFarmacia");

        if (valor instanceof Number number) {
            return number.longValue();
        }

        if (valor instanceof String texto && !texto.trim().isEmpty()) {
            try {
                return Long.parseLong(texto.trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }
}
