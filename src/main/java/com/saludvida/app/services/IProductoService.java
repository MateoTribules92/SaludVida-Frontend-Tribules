package com.saludvida.app.services;

import java.util.List;
import java.util.Optional;
import com.saludvida.app.model.dto.request.ProductoRequestDTO;
import com.saludvida.app.model.dto.response.ProductoResponseDTO;

public interface IProductoService {
    List<ProductoResponseDTO> listar();
    Optional<ProductoResponseDTO> buscarPorId(long id);
    ProductoResponseDTO crear(ProductoRequestDTO dto);
    ProductoResponseDTO actualizar(long id, ProductoRequestDTO dto);
    void activar(long id);
    void desactivar(long id);
}
