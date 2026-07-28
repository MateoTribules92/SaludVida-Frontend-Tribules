package com.saludvida.app.controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.saludvida.app.model.dto.response.FarmaciaResponseDTO;
import com.saludvida.app.model.dto.response.InventarioResponseDTO;
import com.saludvida.app.model.dto.response.ProductoResponseDTO;
import com.saludvida.app.model.enums.EstadoPedido;
import com.saludvida.app.model.enums.EstadoRuta;

import com.saludvida.app.services.*;

@Controller
public class HomeController {
    private final IProductoService productoService;
    private final IInventarioService inventarioService;
    private final IPedidoService pedidoService;
    private final IVehiculoService vehiculoService;
    private final IClienteService clienteService;
    private final IFarmaciaService farmaciaService;
    private final IRutaService rutaService;

    public HomeController(IProductoService productoService, IInventarioService inventarioService,
                          IPedidoService pedidoService, IVehiculoService vehiculoService,
                          IClienteService clienteService, IFarmaciaService farmaciaService,
                          IRutaService rutaService) {
        this.productoService = productoService;
        this.inventarioService = inventarioService;
        this.pedidoService = pedidoService;
        this.vehiculoService = vehiculoService;
        this.clienteService = clienteService;
        this.farmaciaService = farmaciaService;
        this.rutaService = rutaService;
    }

    @GetMapping({"/", "/home", "/index"})
    public String index(Model model) {
        var productos = productoService.listar();
        var inventarios = inventarioService.listar();
        var pedidos = pedidoService.listar();
        var vehiculos = vehiculoService.listar();
        var clientes = clienteService.listar();
        var farmacias = farmaciaService.listar();
        var rutas = rutaService.listar();

        LocalDate hoy = LocalDate.now();
        LocalDate limiteCaducidad = hoy.plusDays(30);

        long pedidosPendientes = pedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.PENDIENTE)
                .count();
        long pedidosEnProceso = pedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.CONFIRMADO
                        || p.getEstado() == EstadoPedido.EN_PREPARACION
                        || p.getEstado() == EstadoPedido.EN_RUTA)
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

        var inventariosCriticos = inventarios.stream()
                .filter(i -> i.getStock() != null
                        && i.getStockMinimo() != null
                        && i.getStock() <= i.getStockMinimo())
                .sorted(Comparator.comparing(InventarioResponseDTO::getStock, Comparator.nullsLast(Integer::compareTo)))
                .limit(5)
                .toList();

        var productosCaducidad = productos.stream()
                .filter(p -> p.getFechaCaducidad() != null)
                .sorted(Comparator.comparing(ProductoResponseDTO::getFechaCaducidad))
                .limit(5)
                .toList();

        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("totalInventarios", inventarios.size());
        model.addAttribute("totalPedidos", pedidos.size());
        model.addAttribute("totalVehiculos", vehiculos.size());
        model.addAttribute("totalClientes", clientes.size());
        model.addAttribute("totalFarmacias", farmacias.size());
        model.addAttribute("pedidosPendientes", pedidosPendientes);
        model.addAttribute("pedidosEnProceso", pedidosEnProceso);
        model.addAttribute("pedidosCerrados", pedidosCerrados);
        model.addAttribute("stockBajo", stockBajo);
        model.addAttribute("productosVencidos", productosVencidos);
        model.addAttribute("productosPorCaducar", productosPorCaducar);
        model.addAttribute("rutasPlanificadas", rutasPlanificadas);
        model.addAttribute("rutasEnRuta", rutasEnRuta);
        model.addAttribute("inventariosCriticos", inventariosCriticos);
        model.addAttribute("productosCaducidad", productosCaducidad);
        model.addAttribute("nombresProductos", nombresProductos);
        model.addAttribute("nombresFarmacias", nombresFarmacias);
        return "index/home";
    }
}

