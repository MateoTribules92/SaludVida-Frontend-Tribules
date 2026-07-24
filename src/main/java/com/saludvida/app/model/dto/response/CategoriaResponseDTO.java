package com.saludvida.app.model.dto.response;
import lombok.Data;
@Data
public class CategoriaResponseDTO {
    private long idCategoria;
    private String nombre;
    private String descripcion;
    private Boolean activo;
}
