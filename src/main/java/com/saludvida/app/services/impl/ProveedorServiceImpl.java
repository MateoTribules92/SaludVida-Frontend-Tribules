package com.saludvida.app.services.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.saludvida.app.model.dto.request.ProveedorRequestDTO;
import com.saludvida.app.model.dto.response.ProveedorResponseDTO;
import com.saludvida.app.services.IProveedorService;

@Service
public class ProveedorServiceImpl extends ClientSupport implements IProveedorService {

    public ProveedorServiceImpl(WebClient webClient) {
        super(webClient);
    }

    @Override
    public List<ProveedorResponseDTO> listar() {
        return getList("/proveedores", new ParameterizedTypeReference<List<ProveedorResponseDTO>>() {});
    }

    @Override
    public Optional<ProveedorResponseDTO> buscarPorId(long id) {
        return getOne("/proveedores/" + id, ProveedorResponseDTO.class);
    }

    @Override
    public ProveedorResponseDTO crear(ProveedorRequestDTO dto) {
        return post("/proveedores", dto, ProveedorResponseDTO.class);
    }

    @Override
    public ProveedorResponseDTO actualizar(long id, ProveedorRequestDTO dto) {
        return put("/proveedores/" + id, dto, ProveedorResponseDTO.class);
    }

    @Override
    public void activar(long id) {
        patch("/proveedores/" + id + "/activar");
    }

    @Override
    public void desactivar(long id) {
        patch("/proveedores/" + id + "/desactivar");
    }
}
