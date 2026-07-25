package com.saludvida.app.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.saludvida.app.model.dto.request.MovimientoInventarioRequestDTO;
import com.saludvida.app.model.dto.response.MovimientoInventarioResponseDTO;
import com.saludvida.app.model.enums.TipoMovimiento;
import com.saludvida.app.services.IMovimientoInventarioService;

@Service
public class MovimientoInventarioServiceImpl extends ClientSupport implements IMovimientoInventarioService {

    public MovimientoInventarioServiceImpl(WebClient webClient) {
        super(webClient);
    }

    @Override
    public List<MovimientoInventarioResponseDTO> listar() {
        return getList("/movimientos-inventario",
                new ParameterizedTypeReference<List<MovimientoInventarioResponseDTO>>() {});
    }

    @Override
    public Optional<MovimientoInventarioResponseDTO> buscarPorId(long id) {
        return getOne("/movimientos-inventario/" + id, MovimientoInventarioResponseDTO.class);
    }

    @Override
    public MovimientoInventarioResponseDTO registrar(MovimientoInventarioRequestDTO dto) {
        return post("/movimientos-inventario", dto, MovimientoInventarioResponseDTO.class);
    }

    @Override
    public List<MovimientoInventarioResponseDTO> buscarPorInventario(long idInventario) {
        return getList("/movimientos-inventario/inventario/" + idInventario,
                new ParameterizedTypeReference<List<MovimientoInventarioResponseDTO>>() {});
    }

    @Override
    public List<MovimientoInventarioResponseDTO> buscarPorTipoMovimiento(TipoMovimiento tipoMovimiento) {
        return getList("/movimientos-inventario/tipo/" + tipoMovimiento,
                new ParameterizedTypeReference<List<MovimientoInventarioResponseDTO>>() {});
    }
}
