package com.saludvida.app.services;

import java.util.List;
import java.util.Optional;
import com.saludvida.app.model.dto.request.RolRequestDTO;
import com.saludvida.app.model.dto.response.RolResponseDTO;

public interface IRolService {
    List<RolResponseDTO> listar();
    Optional<RolResponseDTO> buscarPorId(long id);
    RolResponseDTO crear(RolRequestDTO dto);
    RolResponseDTO actualizar(long id, RolRequestDTO dto);
    void activar(long id);
    void desactivar(long id);
}
