package com.saludvida.app.services.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.saludvida.app.model.dto.request.VehiculoRequestDTO;
import com.saludvida.app.model.dto.response.VehiculoResponseDTO;
import com.saludvida.app.services.IVehiculoService;

@Service
public class VehiculoServiceImpl extends ClientSupport implements IVehiculoService {

    public VehiculoServiceImpl(WebClient webClient) {
        super(webClient);
    }

    @Override
    public List<VehiculoResponseDTO> listar() {
        return getList("/vehiculos", new ParameterizedTypeReference<List<VehiculoResponseDTO>>() {});
    }

    @Override
    public Optional<VehiculoResponseDTO> buscarPorId(long id) {
        return getOne("/vehiculos/" + id, VehiculoResponseDTO.class);
    }

    @Override
    public VehiculoResponseDTO crear(VehiculoRequestDTO dto) {
        return post("/vehiculos", dto, VehiculoResponseDTO.class);
    }

    @Override
    public VehiculoResponseDTO actualizar(long id, VehiculoRequestDTO dto) {
        return put("/vehiculos/" + id, dto, VehiculoResponseDTO.class);
    }

    @Override
    public void activar(long id) {
        patch("/vehiculos/" + id + "/activar");
    }

    @Override
    public void desactivar(long id) {
        patch("/vehiculos/" + id + "/desactivar");
    }
}
