package com.saludvida.app.model.dto.request;

import java.math.BigDecimal;
import com.saludvida.app.model.enums.EstadoPedido;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PedidoRequestDTO {

    @Size(min = 3, max = 30, message = "El número de pedido debe tener entre 3 y 30 caracteres")
    private String numeroPedido;

    @Positive(message = "Debe seleccionar un cliente")
    private long idCliente;

    @Positive(message = "Debe seleccionar una farmacia")
    private long idFarmacia;

    @Positive(message = "Debe seleccionar un vendedor")
    private long idVendedor;

    @NotNull(message = "Debe seleccionar un estado")
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @DecimalMin(value = "0.00", message = "El total no puede ser negativo")
    private BigDecimal total;

    @NotBlank(message = "La dirección de entrega es obligatoria")
    @Size(max = 500, message = "La dirección de entrega no debe superar los 500 caracteres")
    private String direccionEntrega;

    @Size(max = 500, message = "La observación no debe superar los 500 caracteres")
    private String observacion;
}
