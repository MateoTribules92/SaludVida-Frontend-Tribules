package com.saludvida.app.model.dto.request;

import java.time.LocalDate;

import com.saludvida.app.model.enums.EstadoRuta;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RutaRequestDTO {

    private String codigoRuta;

    @NotNull(message = "La fecha de ruta es obligatoria.")
    @FutureOrPresent(message = "La fecha de ruta no puede ser anterior a la fecha actual.")
    private LocalDate fechaRuta;

    @NotBlank(message = "La zona es obligatoria.")
    @Size(max = 80, message = "La zona no debe superar los 80 caracteres.")
    private String zona;

    @Min(value = 1, message = "Debe seleccionar un vehículo.")
    private long idVehiculo;

    @Min(value = 1, message = "Debe seleccionar un distribuidor.")
    private long idDistribuidor;

    @NotNull(message = "El estado es obligatorio.")
    private EstadoRuta estado = EstadoRuta.PLANIFICADA;

    private String observacion;
}
