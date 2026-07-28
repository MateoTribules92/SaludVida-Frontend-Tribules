package com.saludvida.app.services;

import java.util.List;

import com.saludvida.app.model.dto.response.HistorialEstadoPedidoResponseDTO;

public interface IHistorialEstadoPedidoService {

    List<HistorialEstadoPedidoResponseDTO> listar();

    List<HistorialEstadoPedidoResponseDTO> buscarPorPedido(long idPedido);

    List<HistorialEstadoPedidoResponseDTO> buscarPorUsuario(long idUsuario);
}
