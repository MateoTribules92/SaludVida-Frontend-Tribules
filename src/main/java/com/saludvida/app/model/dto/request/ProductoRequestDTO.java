package com.saludvida.app.model.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductoRequestDTO {

    @NotBlank(message = "El codigo del producto es obligatorio")
    @Size(min = 3, max = 50, message = "El codigo debe tener entre 3 y 50 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripcion no debe superar los 500 caracteres")
    private String descripcion;

    @Positive(message = "Debe seleccionar una categoria")
    private long idCategoria;

    @Positive(message = "Debe seleccionar un proveedor")
    private long idProveedor;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
    private BigDecimal precio;

    @NotNull(message = "La fecha de caducidad es obligatoria")
    @FutureOrPresent(message = "No se puede registrar un producto ya vencido")
    private LocalDate fechaCaducidad;

    private Boolean activo = true;
}
