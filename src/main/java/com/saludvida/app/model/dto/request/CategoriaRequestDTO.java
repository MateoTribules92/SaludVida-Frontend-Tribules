package com.saludvida.app.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoriaRequestDTO {

    @NotBlank(message = "El nombre de la categoria es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripcion no debe superar los 500 caracteres")
    private String descripcion;

    private Boolean activo = true;
}
