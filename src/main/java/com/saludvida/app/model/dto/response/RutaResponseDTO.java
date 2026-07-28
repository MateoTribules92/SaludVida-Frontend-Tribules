package com.saludvida.app.model.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.saludvida.app.model.enums.EstadoRuta;

import lombok.Data;

@Data
public class RutaResponseDTO {

    private long idRuta;
    private String codigoRuta;
    private LocalDate fechaRuta;
    private String zona;
    private long idVehiculo;
    private long idDistribuidor;
    private EstadoRuta estado;
    private String observacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
