package com.saludvida.app.model.dto.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DetallePedidoResponseDTO {

    private long idDetalle;
    private long idPedido;
    private long idProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
