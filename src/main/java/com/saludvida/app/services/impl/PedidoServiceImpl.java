package com.saludvida.app.services.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.saludvida.app.model.dto.request.PedidoRequestDTO;
import com.saludvida.app.model.dto.response.PedidoResponseDTO;
import com.saludvida.app.model.enums.EstadoPedido;
import com.saludvida.app.services.IPedidoService;

@Service
public class PedidoServiceImpl extends ClientSupport implements IPedidoService {
    public PedidoServiceImpl(WebClient webClient) { super(webClient); }
    @Override public List<PedidoResponseDTO> listar() { return getList("/pedidos", new ParameterizedTypeReference<List<PedidoResponseDTO>>() {}); }
    @Override public Optional<PedidoResponseDTO> buscarPorId(long id) { return getOne("/pedidos/" + id, PedidoResponseDTO.class); }
    @Override public PedidoResponseDTO crear(PedidoRequestDTO dto) { return post("/pedidos", dto, PedidoResponseDTO.class); }
    @Override public PedidoResponseDTO actualizar(long id, PedidoRequestDTO dto) { return put("/pedidos/" + id, dto, PedidoResponseDTO.class); }
    @Override public void cambiarEstado(long id, EstadoPedido estado) { patch("/pedidos/" + id + "/estado?estado=" + estado.name()); }
}
