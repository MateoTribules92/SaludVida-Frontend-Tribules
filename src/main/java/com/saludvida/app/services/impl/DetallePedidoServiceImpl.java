package com.saludvida.app.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.saludvida.app.model.dto.request.DetallePedidoRequestDTO;
import com.saludvida.app.model.dto.response.DetallePedidoResponseDTO;
import com.saludvida.app.services.IDetallePedidoService;

@Service
public class DetallePedidoServiceImpl extends ClientSupport implements IDetallePedidoService {

    public DetallePedidoServiceImpl(WebClient webClient) {
        super(webClient);
    }

    @Override
    public List<DetallePedidoResponseDTO> listar() {
        return getList("/detalles-pedido", new ParameterizedTypeReference<List<DetallePedidoResponseDTO>>() {});
    }

    @Override
    public Optional<DetallePedidoResponseDTO> buscarPorId(long id) {
        return getOne("/detalles-pedido/" + id, DetallePedidoResponseDTO.class);
    }

    @Override
    public DetallePedidoResponseDTO guardar(DetallePedidoRequestDTO dto) {
        return post("/detalles-pedido", dto, DetallePedidoResponseDTO.class);
    }

    @Override
    public DetallePedidoResponseDTO actualizar(long id, DetallePedidoRequestDTO dto) {
        return put("/detalles-pedido/" + id, dto, DetallePedidoResponseDTO.class);
    }

    @Override
    public void eliminar(long id) {
        delete("/detalles-pedido/" + id);
    }

    @Override
    public List<DetallePedidoResponseDTO> buscarPorPedido(long idPedido) {
        return getList("/detalles-pedido/pedido/" + idPedido,
                new ParameterizedTypeReference<List<DetallePedidoResponseDTO>>() {});
    }

    @Override
    public List<DetallePedidoResponseDTO> buscarPorProducto(long idProducto) {
        return getList("/detalles-pedido/producto/" + idProducto,
                new ParameterizedTypeReference<List<DetallePedidoResponseDTO>>() {});
    }
}
