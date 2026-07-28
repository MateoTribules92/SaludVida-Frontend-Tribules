package com.saludvida.app.services;

import java.util.List;
import java.util.Optional;
import com.saludvida.app.model.dto.request.VehiculoRequestDTO;
import com.saludvida.app.model.dto.response.VehiculoResponseDTO;

public interface IVehiculoService {
    List<VehiculoResponseDTO> listar();
    Optional<VehiculoResponseDTO> buscarPorId(long id);
    VehiculoResponseDTO crear(VehiculoRequestDTO dto);
    VehiculoResponseDTO actualizar(long id, VehiculoRequestDTO dto);
    void activar(long id);
    void desactivar(long id);
}
