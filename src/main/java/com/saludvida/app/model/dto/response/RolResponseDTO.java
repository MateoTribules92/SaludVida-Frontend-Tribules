package com.saludvida.app.model.dto.response;
import lombok.Data;
@Data
public class RolResponseDTO {
    private long idRol;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Boolean activo;
}
