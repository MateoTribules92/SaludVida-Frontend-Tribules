package com.saludvida.app.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.saludvida.app.model.dto.request.UsuarioRequestDTO;
import com.saludvida.app.model.dto.response.UsuarioResponseDTO;
import com.saludvida.app.services.IUsuarioService;

@Service
public class UsuarioServiceImpl extends ClientSupport implements IUsuarioService {

    public UsuarioServiceImpl(WebClient webClient) {
        super(webClient);
    }

    @Override
    public List<UsuarioResponseDTO> listar() {
        return getList("/usuarios", new ParameterizedTypeReference<List<UsuarioResponseDTO>>() {});
    }

    @Override
    public Optional<UsuarioResponseDTO> buscarPorId(long id) {
        return getOne("/usuarios/" + id, UsuarioResponseDTO.class);
    }

    @Override
    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        return post("/usuarios", dto, UsuarioResponseDTO.class);
    }

    @Override
    public UsuarioResponseDTO actualizar(long id, UsuarioRequestDTO dto) {
        return put("/usuarios/" + id, dto, UsuarioResponseDTO.class);
    }

    @Override
    public void activar(long id) {
        patch("/usuarios/" + id + "/activar");
    }

    @Override
    public void desactivar(long id) {
        patch("/usuarios/" + id + "/desactivar");
    }
}
