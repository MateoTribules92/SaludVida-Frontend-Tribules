package com.saludvida.app.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.saludvida.app.model.dto.request.RutaPedidoRequestDTO;
import com.saludvida.app.model.dto.response.RutaPedidoResponseDTO;
import com.saludvida.app.services.IRutaPedidoService;

@Service
public class RutaPedidoServiceImpl extends ClientSupport implements IRutaPedidoService {

    public RutaPedidoServiceImpl(WebClient webClient) {
        super(webClient);
    }

    @Override
    public List<RutaPedidoResponseDTO> listar() {
        return getList("/ruta-pedidos", new ParameterizedTypeReference<List<RutaPedidoResponseDTO>>() {});
    }

    @Override
    public List<RutaPedidoResponseDTO> buscarPorRuta(long idRuta) {
        return getList("/ruta-pedidos/ruta/" + idRuta, new ParameterizedTypeReference<List<RutaPedidoResponseDTO>>() {});
    }

    @Override
    public List<RutaPedidoResponseDTO> buscarPorPedido(long idPedido) {
        return getList("/ruta-pedidos/pedido/" + idPedido, new ParameterizedTypeReference<List<RutaPedidoResponseDTO>>() {});
    }

    @Override
    public Optional<RutaPedidoResponseDTO> buscarPorId(long id) {
        return getOne("/ruta-pedidos/" + id, RutaPedidoResponseDTO.class);
    }

    @Override
    public RutaPedidoResponseDTO asignar(RutaPedidoRequestDTO dto) {
        return post("/ruta-pedidos", dto, RutaPedidoResponseDTO.class);
    }

    @Override
    public RutaPedidoResponseDTO actualizar(long id, RutaPedidoRequestDTO dto) {
        return put("/ruta-pedidos/" + id, dto, RutaPedidoResponseDTO.class);
    }
}
