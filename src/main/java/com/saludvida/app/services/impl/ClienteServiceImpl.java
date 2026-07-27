package com.saludvida.app.services.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.saludvida.app.model.dto.request.ClienteRequestDTO;
import com.saludvida.app.model.dto.response.ClienteResponseDTO;
import com.saludvida.app.services.IClienteService;

@Service
public class ClienteServiceImpl extends ClientSupport implements IClienteService {

    public ClienteServiceImpl(WebClient webClient) {
        super(webClient);
    }

    @Override
    public List<ClienteResponseDTO> listar() {
        return getList("/clientes", new ParameterizedTypeReference<List<ClienteResponseDTO>>() {});
    }

    @Override
    public Optional<ClienteResponseDTO> buscarPorId(long id) {
        return getOne("/clientes/" + id, ClienteResponseDTO.class);
    }

    @Override
    public ClienteResponseDTO crear(ClienteRequestDTO dto) {
        return post("/clientes", dto, ClienteResponseDTO.class);
    }

    @Override
    public ClienteResponseDTO actualizar(long id, ClienteRequestDTO dto) {
        return put("/clientes/" + id, dto, ClienteResponseDTO.class);
    }

    @Override
    public void activar(long id) {
        patch("/clientes/" + id + "/activar");
    }

    @Override
    public void desactivar(long id) {
        patch("/clientes/" + id + "/desactivar");
    }
}
