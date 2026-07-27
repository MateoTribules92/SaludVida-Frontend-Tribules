package com.saludvida.app.model.dto.request;

import java.math.BigDecimal;
import com.saludvida.app.model.enums.ClasificacionCliente;
import com.saludvida.app.model.enums.TipoCliente;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteRequestDTO {

    @NotNull(message = "Debe seleccionar el tipo de cliente")
    private TipoCliente tipoCliente;

    @NotBlank(message = "La identificación es obligatoria")
    @Pattern(regexp = "^[0-9]{10,13}$", message = "La identificación debe tener entre 10 y 13 dígitos")
    private String identificacion;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 3, max = 150, message = "Los nombres deben tener entre 3 y 150 caracteres")
    private String nombres;

    @Email(message = "Ingrese un correo válido")
    @Size(max = 150, message = "El correo no debe superar los 150 caracteres")
    private String correo;

    @Pattern(regexp = "^$|^[0-9+()\\- ]{7,30}$", message = "Ingrese un teléfono válido")
    private String telefono;

    @Size(max = 500, message = "La dirección no debe superar los 500 caracteres")
    private String direccion;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 80, message = "La ciudad no debe superar los 80 caracteres")
    private String ciudad;

    @NotBlank(message = "La zona es obligatoria")
    @Size(max = 80, message = "La zona no debe superar los 80 caracteres")
    private String zona;

    @DecimalMin(value = "-90.0", message = "La latitud mínima es -90")
    @DecimalMax(value = "90.0", message = "La latitud máxima es 90")
    private BigDecimal latitud;

    @DecimalMin(value = "-180.0", message = "La longitud mínima es -180")
    @DecimalMax(value = "180.0", message = "La longitud máxima es 180")
    private BigDecimal longitud;

    @NotNull(message = "Debe seleccionar la clasificación")
    private ClasificacionCliente clasificacion;

    private Boolean activo = true;
}
