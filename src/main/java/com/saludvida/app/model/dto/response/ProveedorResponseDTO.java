package com.saludvida.app.model.dto.response;
import lombok.Data;
@Data
public class ProveedorResponseDTO {
    private long idProveedor;
    private String nombre;
    private String ruc;
    private String telefono;
    private String correo;
    private String direccion;
    private Boolean activo;
}
