package com.saludvida.app.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRequestDTO {

    @Positive(message = "Debe seleccionar un rol")
    private long idRol;

    @Positive(message = "Debe seleccionar una farmacia")
    private long idFarmacia;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 3, max = 120, message = "Los nombres deben tener entre 3 y 120 caracteres")
    private String nombres;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingrese un correo válido")
    @Size(max = 150, message = "El correo no debe superar los 150 caracteres")
    private String correo;

    @Size(min = 6, max = 255, message = "La contraseña debe tener al menos 6 caracteres")
    private String passwordHash;

    private Boolean activo = true;
}
