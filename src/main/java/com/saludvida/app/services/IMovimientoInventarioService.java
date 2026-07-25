package com.saludvida.app.services;

import java.util.List;
import java.util.Optional;

import com.saludvida.app.model.dto.request.MovimientoInventarioRequestDTO;
import com.saludvida.app.model.dto.response.MovimientoInventarioResponseDTO;
import com.saludvida.app.model.enums.TipoMovimiento;

public interface IMovimientoInventarioService {

    List<MovimientoInventarioResponseDTO> listar();

    Optional<MovimientoInventarioResponseDTO> buscarPorId(long id);

    MovimientoInventarioResponseDTO registrar(MovimientoInventarioRequestDTO dto);

    List<MovimientoInventarioResponseDTO> buscarPorInventario(long idInventario);

    List<MovimientoInventarioResponseDTO> buscarPorTipoMovimiento(TipoMovimiento tipoMovimiento);
}
