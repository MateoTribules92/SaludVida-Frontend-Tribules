package com.saludvida.app.services.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.saludvida.app.model.dto.request.ProductoRequestDTO;
import com.saludvida.app.model.dto.response.ProductoResponseDTO;
import com.saludvida.app.services.IProductoService;

@Service
public class ProductoServiceImpl extends ClientSupport implements IProductoService {

    public ProductoServiceImpl(WebClient webClient) {
        super(webClient);
    }

    @Override
    public List<ProductoResponseDTO> listar() {
        return getList("/productos", new ParameterizedTypeReference<List<ProductoResponseDTO>>() {});
    }

    @Override
    public Optional<ProductoResponseDTO> buscarPorId(long id) {
        return getOne("/productos/" + id, ProductoResponseDTO.class);
    }

    @Override
    public ProductoResponseDTO crear(ProductoRequestDTO dto) {
        return post("/productos", dto, ProductoResponseDTO.class);
    }

    @Override
    public ProductoResponseDTO actualizar(long id, ProductoRequestDTO dto) {
        return put("/productos/" + id, dto, ProductoResponseDTO.class);
    }

    @Override
    public void activar(long id) {
        patch("/productos/" + id + "/activar");
    }

    @Override
    public void desactivar(long id) {
        patch("/productos/" + id + "/desactivar");
    }
}
