package com.saludvida.app.model.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.saludvida.app.model.enums.EstadoPedido;
import lombok.Data;
@Data
public class PedidoResponseDTO {
    private long idPedido;
    private String numeroPedido;
    private long idCliente;
    private long idFarmacia;
    private long idVendedor;
    private EstadoPedido estado;
    private BigDecimal total;
    private String direccionEntrega;
    private String observacion;
    private LocalDateTime fechaPedido;
    private LocalDateTime fechaActualizacion;
}
