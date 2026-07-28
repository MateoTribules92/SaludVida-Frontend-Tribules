package com.saludvida.app.model.dto.request;

import java.math.BigDecimal;
import com.saludvida.app.model.enums.EstadoVehiculo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VehiculoRequestDTO {

    @NotBlank(message = "La placa es obligatoria.")
    @Size(max = 20, message = "La placa no debe superar los 20 caracteres.")
    @Pattern(regexp = "^[A-Za-z0-9-]{6,20}$", message = "La placa debe tener entre 6 y 20 caracteres alfanuméricos o guion.")
    private String placa;

    @Size(max = 150, message = "La descripción no debe superar los 150 caracteres.")
    private String descripcion;

    @NotNull(message = "La capacidad de pedidos es obligatoria.")
    @Min(value = 1, message = "La capacidad de pedidos debe ser mayor a cero.")
    private Integer capacidadPedidos;

    @NotNull(message = "La capacidad en kilogramos es obligatoria.")
    @DecimalMin(value = "0.01", message = "La capacidad en kilogramos debe ser mayor a cero.")
    private BigDecimal capacidadKg;

    @NotNull(message = "El estado es obligatorio.")
    private EstadoVehiculo estado = EstadoVehiculo.DISPONIBLE;

    private Boolean activo = true;
}
