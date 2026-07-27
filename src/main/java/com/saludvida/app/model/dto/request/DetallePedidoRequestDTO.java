package com.saludvida.app.model.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DetallePedidoRequestDTO {

    @Positive(message = "Debe seleccionar un pedido")
    private long idPedido;

    @Positive(message = "Debe seleccionar un producto")
    private long idProducto;

    @NotNull(message = "Debe ingresar la cantidad")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;

    @NotNull(message = "Debe ingresar el precio unitario")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a cero")
    private BigDecimal precioUnitario;

    private BigDecimal subtotal;
}
