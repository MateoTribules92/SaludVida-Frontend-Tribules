package com.saludvida.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticModuleController {
    @GetMapping("/movimientos") public String movimientos() { return "inventario/movimientos"; }
    @GetMapping("/rutas") public String rutas() { return "distribucion/rutas"; }
    @GetMapping("/usuarios") public String usuarios() { return "administracion/usuarios"; }
    @GetMapping("/roles") public String roles() { return "administracion/roles"; }
    @GetMapping("/historial-estados") public String historialEstados() { return "ventas-pedidos/historialestados"; }
    @GetMapping("/reportes") public String reportes() { return "reportes/reporte"; }
}
