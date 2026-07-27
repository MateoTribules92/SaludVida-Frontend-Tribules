package com.saludvida.app.services;

import java.util.List;
import java.util.Optional;

import com.saludvida.app.model.dto.request.UsuarioRequestDTO;
import com.saludvida.app.model.dto.response.UsuarioResponseDTO;

public interface IUsuarioService {

    List<UsuarioResponseDTO> listar();

    Optional<UsuarioResponseDTO> buscarPorId(long id);

    UsuarioResponseDTO crear(UsuarioRequestDTO dto);

    UsuarioResponseDTO actualizar(long id, UsuarioRequestDTO dto);

    void activar(long id);

    void desactivar(long id);
}
