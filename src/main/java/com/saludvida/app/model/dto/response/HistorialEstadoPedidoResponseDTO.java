package com.saludvida.app.model.dto.response;

import java.time.LocalDateTime;

import com.saludvida.app.model.enums.EstadoPedido;

import lombok.Data;

@Data
public class HistorialEstadoPedidoResponseDTO {

    private long idHistorial;
    private long idPedido;
    private EstadoPedido estadoAnterior;
    private EstadoPedido estadoNuevo;
    private Long idUsuario;
    private String observacion;
    private LocalDateTime fechaRegistro;
}
