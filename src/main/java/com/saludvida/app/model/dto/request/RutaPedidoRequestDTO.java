package com.saludvida.app.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RutaPedidoRequestDTO {

    @Min(value = 1, message = "Debe seleccionar una ruta.")
    private long idRuta;

    @Min(value = 1, message = "Debe seleccionar un pedido.")
    private long idPedido;

    @NotNull(message = "El orden de entrega es obligatorio.")
    @Min(value = 1, message = "El orden de entrega debe ser mayor a cero.")
    private Integer ordenEntrega = 1;
}
