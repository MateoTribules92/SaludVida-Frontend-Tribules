package com.saludvida.app.model.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UsuarioResponseDTO {

    private long idUsuario;
    private long idRol;
    private String nombres;
    private String correo;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
