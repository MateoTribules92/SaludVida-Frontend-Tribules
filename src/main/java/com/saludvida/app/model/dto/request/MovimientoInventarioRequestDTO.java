package com.saludvida.app.model.dto.request;

import com.saludvida.app.model.enums.TipoMovimiento;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MovimientoInventarioRequestDTO {

    @Positive(message = "Debe seleccionar un inventario")
    private long idInventario;

    private Long idUsuario;

    @NotNull(message = "Debe seleccionar el tipo de movimiento")
    private TipoMovimiento tipoMovimiento;

    @NotNull(message = "Debe ingresar la cantidad")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;

    private String motivo;
}
