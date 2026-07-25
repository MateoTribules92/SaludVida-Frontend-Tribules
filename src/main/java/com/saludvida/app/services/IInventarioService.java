package com.saludvida.app.services;

import java.util.List;
import java.util.Optional;
import com.saludvida.app.model.dto.request.InventarioRequestDTO;
import com.saludvida.app.model.dto.response.InventarioResponseDTO;

public interface IInventarioService {
    List<InventarioResponseDTO> listar();
    Optional<InventarioResponseDTO> buscarPorId(long id);
    InventarioResponseDTO guardar(InventarioRequestDTO dto);
    InventarioResponseDTO actualizar(long id, InventarioRequestDTO dto);
}
