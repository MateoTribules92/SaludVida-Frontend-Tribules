package com.saludvida.app.model.dto.response;

import lombok.Data;

@Data
public class LoginResponseDTO {

    private long idUsuario;
    private long idRol;
    private long idFarmacia;
    private String nombres;
    private String correo;
    private String codigoRol;
    private String nombreRol;
}
