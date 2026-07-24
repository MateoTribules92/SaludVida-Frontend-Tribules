package com.saludvida.app.services;

import java.util.List;
import java.util.Optional;
import com.saludvida.app.model.dto.request.CategoriaRequestDTO;
import com.saludvida.app.model.dto.response.CategoriaResponseDTO;

public interface ICategoriaService {
    List<CategoriaResponseDTO> listar();
    Optional<CategoriaResponseDTO> buscarPorId(long id);
    CategoriaResponseDTO crear(CategoriaRequestDTO dto);
    CategoriaResponseDTO actualizar(long id, CategoriaRequestDTO dto);
    void activar(long id);
    void desactivar(long id);
}
