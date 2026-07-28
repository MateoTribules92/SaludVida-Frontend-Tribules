package com.saludvida.app.model.dto.response;
import java.math.BigDecimal;
import com.saludvida.app.model.enums.EstadoVehiculo;
import lombok.Data;
@Data
public class VehiculoResponseDTO {
    private long idVehiculo;
    private String placa;
    private String descripcion;
    private Integer capacidadPedidos;
    private BigDecimal capacidadKg;
    private EstadoVehiculo estado;
    private Boolean activo;
}
