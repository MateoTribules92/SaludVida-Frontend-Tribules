package com.saludvida.app.model.dto.response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
@Data
public class ProductoResponseDTO {
    private long idProducto;
    private String codigo;
    private String nombre;
    private String descripcion;
    private long idCategoria;
    private long idProveedor;
    private BigDecimal precio;
    private LocalDate fechaCaducidad;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
