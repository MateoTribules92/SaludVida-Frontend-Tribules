package com.saludvida.app.services.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.saludvida.app.model.dto.response.HistorialEstadoPedidoResponseDTO;
import com.saludvida.app.services.IHistorialEstadoPedidoService;

@Service
public class HistorialEstadoPedidoServiceImpl extends ClientSupport implements IHistorialEstadoPedidoService {

    public HistorialEstadoPedidoServiceImpl(WebClient webClient) {
        super(webClient);
    }

    @Override
    public List<HistorialEstadoPedidoResponseDTO> listar() {
        return getList("/historial-estados-pedido",
                new ParameterizedTypeReference<List<HistorialEstadoPedidoResponseDTO>>() {});
    }

    @Override
    public List<HistorialEstadoPedidoResponseDTO> buscarPorPedido(long idPedido) {
        return getList("/historial-estados-pedido/pedido/" + idPedido,
                new ParameterizedTypeReference<List<HistorialEstadoPedidoResponseDTO>>() {});
    }

    @Override
    public List<HistorialEstadoPedidoResponseDTO> buscarPorUsuario(long idUsuario) {
        return getList("/historial-estados-pedido/usuario/" + idUsuario,
                new ParameterizedTypeReference<List<HistorialEstadoPedidoResponseDTO>>() {});
    }
}
