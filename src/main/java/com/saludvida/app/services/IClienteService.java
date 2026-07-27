package com.saludvida.app.services;

import java.util.List;
import java.util.Optional;
import com.saludvida.app.model.dto.request.ClienteRequestDTO;
import com.saludvida.app.model.dto.response.ClienteResponseDTO;

public interface IClienteService {
    List<ClienteResponseDTO> listar();
    Optional<ClienteResponseDTO> buscarPorId(long id);
    ClienteResponseDTO crear(ClienteRequestDTO dto);
    ClienteResponseDTO actualizar(long id, ClienteRequestDTO dto);
    void activar(long id);
    void desactivar(long id);
}
