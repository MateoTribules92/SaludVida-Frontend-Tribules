package com.saludvida.app.model.dto.response;
import java.math.BigDecimal;
import lombok.Data;
@Data
public class FarmaciaResponseDTO {
    private long idFarmacia;
    private String nombre;
    private String direccion;
    private String ciudad;
    private String zona;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private Boolean activo;
}
