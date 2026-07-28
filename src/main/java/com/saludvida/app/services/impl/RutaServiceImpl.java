package com.saludvida.app.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.saludvida.app.model.dto.request.RutaRequestDTO;
import com.saludvida.app.model.dto.response.RutaResponseDTO;
import com.saludvida.app.model.enums.EstadoRuta;
import com.saludvida.app.services.IRutaService;

@Service
public class RutaServiceImpl extends ClientSupport implements IRutaService {

    public RutaServiceImpl(WebClient webClient) {
        super(webClient);
    }

    @Override
    public List<RutaResponseDTO> listar() {
        return getList("/rutas", new ParameterizedTypeReference<List<RutaResponseDTO>>() {});
    }

    @Override
    public Optional<RutaResponseDTO> buscarPorId(long id) {
        return getOne("/rutas/" + id, RutaResponseDTO.class);
    }

    @Override
    public RutaResponseDTO crear(RutaRequestDTO dto) {
        return post("/rutas", dto, RutaResponseDTO.class);
    }

    @Override
    public RutaResponseDTO actualizar(long id, RutaRequestDTO dto) {
        return put("/rutas/" + id, dto, RutaResponseDTO.class);
    }

    @Override
    public void cambiarEstado(long id, EstadoRuta estado) {
        patch("/rutas/" + id + "/estado?estado=" + estado.name());
    }
}
