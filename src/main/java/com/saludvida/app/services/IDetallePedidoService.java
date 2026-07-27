package com.saludvida.app.services;

import java.util.List;
import java.util.Optional;

import com.saludvida.app.model.dto.request.DetallePedidoRequestDTO;
import com.saludvida.app.model.dto.response.DetallePedidoResponseDTO;

public interface IDetallePedidoService {

    List<DetallePedidoResponseDTO> listar();

    Optional<DetallePedidoResponseDTO> buscarPorId(long id);

    DetallePedidoResponseDTO guardar(DetallePedidoRequestDTO dto);

    DetallePedidoResponseDTO actualizar(long id, DetallePedidoRequestDTO dto);

    void eliminar(long id);

    List<DetallePedidoResponseDTO> buscarPorPedido(long idPedido);

    List<DetallePedidoResponseDTO> buscarPorProducto(long idProducto);
}
