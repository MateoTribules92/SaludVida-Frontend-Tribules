package com.saludvida.app.services.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.saludvida.app.model.dto.request.InventarioRequestDTO;
import com.saludvida.app.model.dto.response.InventarioResponseDTO;
import com.saludvida.app.services.IInventarioService;

@Service
public class InventarioServiceImpl extends ClientSupport implements IInventarioService {

    public InventarioServiceImpl(WebClient webClient) {
        super(webClient);
    }

    @Override
    public List<InventarioResponseDTO> listar() {
        return getList("/inventarios", new ParameterizedTypeReference<List<InventarioResponseDTO>>() {});
    }

    @Override
    public Optional<InventarioResponseDTO> buscarPorId(long id) {
        return getOne("/inventarios/" + id, InventarioResponseDTO.class);
    }

    @Override
    public InventarioResponseDTO guardar(InventarioRequestDTO dto) {
        return post("/inventarios", dto, InventarioResponseDTO.class);
    }

    @Override
    public InventarioResponseDTO actualizar(long id, InventarioRequestDTO dto) {
        return put("/inventarios/" + id, dto, InventarioResponseDTO.class);
    }
}
