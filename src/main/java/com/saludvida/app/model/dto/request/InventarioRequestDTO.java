package com.saludvida.app.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class InventarioRequestDTO {

    @Positive(message = "Debe seleccionar una farmacia")
    private long idFarmacia;

    @Positive(message = "Debe seleccionar un producto")
    private long idProducto;

    @NotNull(message = "Debe ingresar el stock")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "Debe ingresar el stock mínimo")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;
}
