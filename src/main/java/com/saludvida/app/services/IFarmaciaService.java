package com.saludvida.app.services;

import java.util.List;
import java.util.Optional;
import com.saludvida.app.model.dto.request.FarmaciaRequestDTO;
import com.saludvida.app.model.dto.response.FarmaciaResponseDTO;

public interface IFarmaciaService {
    List<FarmaciaResponseDTO> listar();
    Optional<FarmaciaResponseDTO> buscarPorId(long id);
    FarmaciaResponseDTO crear(FarmaciaRequestDTO dto);
    FarmaciaResponseDTO actualizar(long id, FarmaciaRequestDTO dto);
    void activar(long id);
    void desactivar(long id);
}
