package com.saludvida.app.services;

import java.util.Optional;

import com.saludvida.app.model.dto.request.LoginRequestDTO;
import com.saludvida.app.model.dto.response.LoginResponseDTO;

public interface IAuthService {

    Optional<LoginResponseDTO> login(LoginRequestDTO request);
}
