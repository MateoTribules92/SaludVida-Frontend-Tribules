package com.saludvida.app.services.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.saludvida.app.model.dto.request.FarmaciaRequestDTO;
import com.saludvida.app.model.dto.response.FarmaciaResponseDTO;
import com.saludvida.app.services.IFarmaciaService;

@Service
public class FarmaciaServiceImpl extends ClientSupport implements IFarmaciaService {

    public FarmaciaServiceImpl(WebClient webClient) {
        super(webClient);
    }

    @Override
    public List<FarmaciaResponseDTO> listar() {
        return getList("/farmacias", new ParameterizedTypeReference<List<FarmaciaResponseDTO>>() {});
    }

    @Override
    public Optional<FarmaciaResponseDTO> buscarPorId(long id) {
        return getOne("/farmacias/" + id, FarmaciaResponseDTO.class);
    }

    @Override
    public FarmaciaResponseDTO crear(FarmaciaRequestDTO dto) {
        return post("/farmacias", dto, FarmaciaResponseDTO.class);
    }

    @Override
    public FarmaciaResponseDTO actualizar(long id, FarmaciaRequestDTO dto) {
        return put("/farmacias/" + id, dto, FarmaciaResponseDTO.class);
    }

    @Override
    public void activar(long id) {
        patch("/farmacias/" + id + "/activar");
    }

    @Override
    public void desactivar(long id) {
        patch("/farmacias/" + id + "/desactivar");
    }
}
