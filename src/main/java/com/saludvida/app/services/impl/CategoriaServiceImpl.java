package com.saludvida.app.services.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.saludvida.app.model.dto.request.CategoriaRequestDTO;
import com.saludvida.app.model.dto.response.CategoriaResponseDTO;
import com.saludvida.app.services.ICategoriaService;

@Service
public class CategoriaServiceImpl extends ClientSupport implements ICategoriaService {

    public CategoriaServiceImpl(WebClient webClient) {
        super(webClient);
    }

    @Override
    public List<CategoriaResponseDTO> listar() {
        return getList("/categorias", new ParameterizedTypeReference<List<CategoriaResponseDTO>>() {});
    }

    @Override
    public Optional<CategoriaResponseDTO> buscarPorId(long id) {
        return getOne("/categorias/" + id, CategoriaResponseDTO.class);
    }

    @Override
    public CategoriaResponseDTO crear(CategoriaRequestDTO dto) {
        return post("/categorias", dto, CategoriaResponseDTO.class);
    }

    @Override
    public CategoriaResponseDTO actualizar(long id, CategoriaRequestDTO dto) {
        return put("/categorias/" + id, dto, CategoriaResponseDTO.class);
    }

    @Override
    public void activar(long id) {
        patch("/categorias/" + id + "/activar");
    }

    @Override
    public void desactivar(long id) {
        patch("/categorias/" + id + "/desactivar");
    }
}
