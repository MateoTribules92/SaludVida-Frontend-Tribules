package com.saludvida.app.services.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.saludvida.app.model.dto.request.RolRequestDTO;
import com.saludvida.app.model.dto.response.RolResponseDTO;
import com.saludvida.app.services.IRolService;

@Service
public class RolServiceImpl extends ClientSupport implements IRolService {

    public RolServiceImpl(WebClient webClient) {
        super(webClient);
    }

    @Override
    public List<RolResponseDTO> listar() {
        return getList("/roles", new ParameterizedTypeReference<List<RolResponseDTO>>() {});
    }

    @Override
    public Optional<RolResponseDTO> buscarPorId(long id) {
        return getOne("/roles/" + id, RolResponseDTO.class);
    }

    @Override
    public RolResponseDTO crear(RolRequestDTO dto) {
        return post("/roles", dto, RolResponseDTO.class);
    }

    @Override
    public RolResponseDTO actualizar(long id, RolRequestDTO dto) {
        return put("/roles/" + id, dto, RolResponseDTO.class);
    }

    @Override
    public void activar(long id) {
        patch("/roles/" + id + "/activar");
    }

    @Override
    public void desactivar(long id) {
        patch("/roles/" + id + "/desactivar");
    }
}
