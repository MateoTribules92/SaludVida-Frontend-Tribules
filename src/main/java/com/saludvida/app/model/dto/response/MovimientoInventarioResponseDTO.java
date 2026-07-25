package com.saludvida.app.model.dto.response;

import java.time.LocalDateTime;

import com.saludvida.app.model.enums.TipoMovimiento;

import lombok.Data;

@Data
public class MovimientoInventarioResponseDTO {

    private long idMovimiento;
    private long idInventario;
    private Long idUsuario;
    private TipoMovimiento tipoMovimiento;
    private Integer cantidad;
    private String motivo;
    private LocalDateTime fechaMovimiento;
}
