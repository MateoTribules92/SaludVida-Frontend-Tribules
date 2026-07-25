package com.saludvida.app.model.dto.response;
import java.time.LocalDateTime;
import lombok.Data;
@Data
public class InventarioResponseDTO {
    private long idInventario;
    private long idFarmacia;
    private long idProducto;
    private Integer stock;
    private Integer stockMinimo;
    private LocalDateTime fechaActualizacion;
}
