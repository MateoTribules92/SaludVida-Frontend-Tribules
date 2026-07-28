package com.saludvida.app.services;

import java.util.List;
import java.util.Optional;

import com.saludvida.app.model.dto.request.RutaPedidoRequestDTO;
import com.saludvida.app.model.dto.response.RutaPedidoResponseDTO;

public interface IRutaPedidoService {

    List<RutaPedidoResponseDTO> listar();

    List<RutaPedidoResponseDTO> buscarPorRuta(long idRuta);

    List<RutaPedidoResponseDTO> buscarPorPedido(long idPedido);

    Optional<RutaPedidoResponseDTO> buscarPorId(long id);

    RutaPedidoResponseDTO asignar(RutaPedidoRequestDTO dto);

    RutaPedidoResponseDTO actualizar(long id, RutaPedidoRequestDTO dto);
}
