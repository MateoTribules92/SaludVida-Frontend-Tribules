package com.saludvida.app.services;

import java.util.List;
import java.util.Optional;
import com.saludvida.app.model.dto.request.ProveedorRequestDTO;
import com.saludvida.app.model.dto.response.ProveedorResponseDTO;

public interface IProveedorService {
    List<ProveedorResponseDTO> listar();
    Optional<ProveedorResponseDTO> buscarPorId(long id);
    ProveedorResponseDTO crear(ProveedorRequestDTO dto);
    ProveedorResponseDTO actualizar(long id, ProveedorRequestDTO dto);
    void activar(long id);
    void desactivar(long id);
}
