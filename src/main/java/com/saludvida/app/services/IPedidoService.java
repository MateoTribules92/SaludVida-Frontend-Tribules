package com.saludvida.app.services;

import java.util.List;
import java.util.Optional;
import com.saludvida.app.model.dto.request.PedidoRequestDTO;
import com.saludvida.app.model.dto.response.PedidoResponseDTO;
import com.saludvida.app.model.enums.EstadoPedido;

public interface IPedidoService {
    List<PedidoResponseDTO> listar();
    Optional<PedidoResponseDTO> buscarPorId(long id);
    PedidoResponseDTO crear(PedidoRequestDTO dto);
    PedidoResponseDTO actualizar(long id, PedidoRequestDTO dto);
    void cambiarEstado(long id, EstadoPedido estado);
}
