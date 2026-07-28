package com.saludvida.app.model.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RutaPedidoResponseDTO {

    private long idRutaPedido;
    private long idRuta;
    private long idPedido;
    private Integer ordenEntrega;
    private LocalDateTime fechaAsignacion;
}
