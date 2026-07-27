package com.saludvida.app.model.dto.response;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.saludvida.app.model.enums.ClasificacionCliente;
import com.saludvida.app.model.enums.TipoCliente;
import lombok.Data;
@Data
public class ClienteResponseDTO {
    private long idCliente;
    private TipoCliente tipoCliente;
    private String identificacion;
    private String nombres;
    private String correo;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String zona;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private ClasificacionCliente clasificacion;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
