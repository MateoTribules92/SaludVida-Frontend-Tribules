package com.saludvida.app.model.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FarmaciaRequestDTO {

    @NotBlank(message = "El nombre de la farmacia es obligatorio")
    @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres")
    private String nombre;

    @NotBlank(message = "La direccion es obligatoria")
    @Size(max = 500, message = "La direccion no debe superar los 500 caracteres")
    private String direccion;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 80, message = "La ciudad no debe superar los 80 caracteres")
    private String ciudad;

    @NotBlank(message = "La zona es obligatoria")
    @Size(max = 80, message = "La zona no debe superar los 80 caracteres")
    private String zona;

    @DecimalMin(value = "-90.0", message = "La latitud minima es -90")
    @DecimalMax(value = "90.0", message = "La latitud maxima es 90")
    private BigDecimal latitud;

    @DecimalMin(value = "-180.0", message = "La longitud minima es -180")
    @DecimalMax(value = "180.0", message = "La longitud maxima es 180")
    private BigDecimal longitud;

    private Boolean activo = true;
}
