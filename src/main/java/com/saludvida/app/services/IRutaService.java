package com.saludvida.app.services;

import java.util.List;
import java.util.Optional;

import com.saludvida.app.model.dto.request.RutaRequestDTO;
import com.saludvida.app.model.dto.response.RutaResponseDTO;
import com.saludvida.app.model.enums.EstadoRuta;

public interface IRutaService {

    List<RutaResponseDTO> listar();

    Optional<RutaResponseDTO> buscarPorId(long id);

    RutaResponseDTO crear(RutaRequestDTO dto);

    RutaResponseDTO actualizar(long id, RutaRequestDTO dto);

    void cambiarEstado(long id, EstadoRuta estado);
}
