package com.saludvida.app.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProveedorRequestDTO {

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    private String nombre;

    @NotBlank(message = "El RUC es obligatorio")
    @Pattern(regexp = "^[0-9]{10,13}$", message = "El RUC debe contener solo numeros y tener entre 10 y 13 digitos")
    private String ruc;

    @Pattern(regexp = "^$|^[0-9+()\\- ]{7,30}$", message = "Ingrese un telefono valido")
    private String telefono;

    @Email(message = "Ingrese un correo valido")
    @Size(max = 150, message = "El correo no debe superar los 150 caracteres")
    private String correo;

    @Size(max = 500, message = "La direccion no debe superar los 500 caracteres")
    private String direccion;

    private Boolean activo = true;
}
