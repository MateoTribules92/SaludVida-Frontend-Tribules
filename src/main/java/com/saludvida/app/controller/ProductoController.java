package com.saludvida.app.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.saludvida.app.model.dto.request.ProductoRequestDTO;
import com.saludvida.app.model.dto.response.ProductoResponseDTO;
import com.saludvida.app.model.dto.response.CategoriaResponseDTO;
import com.saludvida.app.model.dto.response.ProveedorResponseDTO;
import com.saludvida.app.services.ICategoriaService;
import com.saludvida.app.services.IProductoService;
import com.saludvida.app.services.IProveedorService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private static final int DIAS_ALERTA_CADUCIDAD = 30;

    private final IProductoService service;
    private final ICategoriaService categoriaService;
    private final IProveedorService proveedorService;

    public ProductoController(
            IProductoService service,
            ICategoriaService categoriaService,
            IProveedorService proveedorService) {
        this.service = service;
        this.categoriaService = categoriaService;
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public String listar(Model model) {
        cargarModeloBase(model);

        if (!model.containsAttribute("request")) {
            model.addAttribute("request", new ProductoRequestDTO());
        }

        if (!model.containsAttribute("editando")) {
            model.addAttribute("editando", false);
        }

        return "inventario/productos";
    }

    @PostMapping
    public String crear(
            @Valid @ModelAttribute("request") ProductoRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            return "inventario/productos";
        }

        try {
            request.setActivo(true);
            service.crear(request);
            redirectAttributes.addFlashAttribute("mensaje", "Producto creado correctamente");
            return "redirect:/productos";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", false);
            model.addAttribute("error", "No se pudo crear el producto. Verifica que el codigo no este duplicado y que categoria/proveedor existan.");
            return "inventario/productos";
        }
    }

    @GetMapping("/{id}/editar")
    public String mostrarEditar(@PathVariable long id, Model model, RedirectAttributes redirectAttributes) {
        var producto = service.buscarPorId(id);

        if (producto.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
            return "redirect:/productos";
        }

        ProductoResponseDTO response = producto.get();
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setCodigo(response.getCodigo());
        request.setNombre(response.getNombre());
        request.setDescripcion(response.getDescripcion());
        request.setIdCategoria(response.getIdCategoria());
        request.setIdProveedor(response.getIdProveedor());
        request.setPrecio(response.getPrecio());
        request.setFechaCaducidad(response.getFechaCaducidad());
        request.setActivo(response.getActivo());

        cargarModeloBase(model);
        model.addAttribute("request", request);
        model.addAttribute("editando", true);
        model.addAttribute("editId", id);

        return "inventario/productos";
    }

    @PostMapping("/{id}")
    public String actualizar(
            @PathVariable long id,
            @Valid @ModelAttribute("request") ProductoRequestDTO request,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            return "inventario/productos";
        }

        try {
            service.actualizar(id, request);
            redirectAttributes.addFlashAttribute("mensaje", "Producto actualizado correctamente");
            return "redirect:/productos";
        } catch (Exception e) {
            cargarModeloBase(model);
            model.addAttribute("editando", true);
            model.addAttribute("editId", id);
            model.addAttribute("error", "No se pudo actualizar el producto.");
            return "inventario/productos";
        }
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable long id, RedirectAttributes redirectAttributes) {
        service.desactivar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Producto desactivado. No se elimino fisicamente por auditoria.");
        return "redirect:/productos";
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable long id, RedirectAttributes redirectAttributes) {
        service.activar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Producto activado correctamente");
        return "redirect:/productos";
    }

    private void cargarModeloBase(Model model) {
        var productos = service.listar();
        var todasCategorias = categoriaService.listar();
        var todosProveedores = proveedorService.listar();

        var categoriasActivas = todasCategorias.stream()
                .filter(c -> Boolean.TRUE.equals(c.getActivo()))
                .toList();

        var proveedoresActivos = todosProveedores.stream()
                .filter(p -> Boolean.TRUE.equals(p.getActivo()))
                .toList();

        var nombresCategorias = todasCategorias.stream()
                .collect(Collectors.toMap(
                        CategoriaResponseDTO::getIdCategoria,
                        CategoriaResponseDTO::getNombre,
                        (nombreActual, nombreRepetido) -> nombreActual
                ));

        var nombresProveedores = todosProveedores.stream()
                .collect(Collectors.toMap(
                        ProveedorResponseDTO::getIdProveedor,
                        ProveedorResponseDTO::getNombre,
                        (nombreActual, nombreRepetido) -> nombreActual
                ));

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriasActivas);
        model.addAttribute("proveedores", proveedoresActivos);
        model.addAttribute("nombresCategorias", nombresCategorias);
        model.addAttribute("nombresProveedores", nombresProveedores);
        model.addAttribute("hoy", LocalDate.now());
        model.addAttribute("limiteCaducidad", LocalDate.now().plusDays(DIAS_ALERTA_CADUCIDAD));
        model.addAttribute("totalVencidos", productos.stream().filter(this::estaVencido).count());
        model.addAttribute("totalProximos", productos.stream().filter(this::estaProximoCaducar).count());
    }

    private boolean estaVencido(ProductoResponseDTO producto) {
        return producto.getFechaCaducidad() != null && producto.getFechaCaducidad().isBefore(LocalDate.now());
    }

    private boolean estaProximoCaducar(ProductoResponseDTO producto) {
        if (producto.getFechaCaducidad() == null || estaVencido(producto)) {
            return false;
        }
        long dias = ChronoUnit.DAYS.between(LocalDate.now(), producto.getFechaCaducidad());
        return dias <= DIAS_ALERTA_CADUCIDAD;
    }
}

