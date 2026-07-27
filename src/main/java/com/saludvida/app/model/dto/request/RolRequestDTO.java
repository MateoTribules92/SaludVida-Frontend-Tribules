package com.saludvida.app.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RolRequestDTO {

    @NotBlank(message = "El código del rol es obligatorio")
    @Pattern(regexp = "^[A-Za-z0-9_]{3,40}$", message = "El código debe tener letras, números o guion bajo, entre 3 y 40 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no debe superar los 500 caracteres")
    private String descripcion;

    private Boolean activo = true;
}
