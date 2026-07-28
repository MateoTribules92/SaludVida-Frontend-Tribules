package com.saludvida.app.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final String ADMIN = "ADMINISTRADOR";
    private static final String ADMIN_ALTERNATIVO = "ADMIN";
    private static final String VENDEDOR = "VENDEDOR";
    private static final String INVENTARIO = "INVENTARIO";
    private static final String DISTRIBUIDOR = "DISTRIBUIDOR";
    private static final String PERSONAL_DISTRIBUCION = "PERSONAL_DISTRIBUCION";

    private static final Map<String, List<String>> PERMISOS_POR_RUTA = crearPermisos();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession(false);
        boolean autenticado = session != null && session.getAttribute("usuarioSesion") != null;

        if (autenticado) {
            String rol = normalizarRol(session.getAttribute("rolUsuario"));

            if (tienePermiso(request.getRequestURI(), request.getContextPath(), rol)) {
                return true;
            }

            response.sendRedirect(request.getContextPath() + "/home?noAutorizado");
            return false;
        }

        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }

    private boolean tienePermiso(String requestUri, String contextPath, String rol) {
        String ruta = requestUri;

        if (contextPath != null && !contextPath.trim().isEmpty() && ruta.startsWith(contextPath)) {
            ruta = ruta.substring(contextPath.length());
        }

        if (rol == null || rol.trim().isEmpty() || "null".equalsIgnoreCase(rol)) {
            return false;
        }

        if (ADMIN.equals(rol) || ADMIN_ALTERNATIVO.equals(rol)) {
            return true;
        }

        for (Map.Entry<String, List<String>> entry : PERMISOS_POR_RUTA.entrySet()) {
            String prefijo = entry.getKey();

            if (ruta.equals(prefijo) || ruta.startsWith(prefijo + "/")) {
                return entry.getValue().contains(rol);
            }
        }

        return false;
    }

    private String normalizarRol(Object rolSesion) {
        if (rolSesion == null) {
            return "";
        }

        return rolSesion.toString().trim().toUpperCase();
    }

    private static Map<String, List<String>> crearPermisos() {
        Map<String, List<String>> permisos = new HashMap<>();

        permisos.put("/home", List.of(ADMIN, ADMIN_ALTERNATIVO, VENDEDOR, INVENTARIO, DISTRIBUIDOR, PERSONAL_DISTRIBUCION));
        permisos.put("/reportes", List.of(ADMIN, ADMIN_ALTERNATIVO, VENDEDOR, INVENTARIO));

        permisos.put("/productos", List.of(ADMIN, ADMIN_ALTERNATIVO, INVENTARIO));
        permisos.put("/inventarios", List.of(ADMIN, ADMIN_ALTERNATIVO, INVENTARIO));
        permisos.put("/movimientos", List.of(ADMIN, ADMIN_ALTERNATIVO, INVENTARIO));
        permisos.put("/categorias", List.of(ADMIN, ADMIN_ALTERNATIVO, INVENTARIO));
        permisos.put("/proveedores", List.of(ADMIN, ADMIN_ALTERNATIVO, INVENTARIO));

        permisos.put("/clientes", List.of(ADMIN, ADMIN_ALTERNATIVO, VENDEDOR));
        permisos.put("/pedidos", List.of(ADMIN, ADMIN_ALTERNATIVO, VENDEDOR, DISTRIBUIDOR, PERSONAL_DISTRIBUCION));
        permisos.put("/detalles-pedido", List.of(ADMIN, ADMIN_ALTERNATIVO, VENDEDOR));
        permisos.put("/historial-estados", List.of(ADMIN, ADMIN_ALTERNATIVO, VENDEDOR));

        permisos.put("/rutas", List.of(ADMIN, ADMIN_ALTERNATIVO, DISTRIBUIDOR, PERSONAL_DISTRIBUCION));
        permisos.put("/ruta-pedidos", List.of(ADMIN, ADMIN_ALTERNATIVO, DISTRIBUIDOR, PERSONAL_DISTRIBUCION));
        permisos.put("/vehiculos", List.of(ADMIN, ADMIN_ALTERNATIVO, DISTRIBUIDOR, PERSONAL_DISTRIBUCION));

        permisos.put("/farmacias", List.of(ADMIN, ADMIN_ALTERNATIVO));
        permisos.put("/usuarios", List.of(ADMIN, ADMIN_ALTERNATIVO));
        permisos.put("/roles", List.of(ADMIN, ADMIN_ALTERNATIVO));

        return permisos;
    }
}
