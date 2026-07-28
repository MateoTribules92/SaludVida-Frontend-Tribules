package com.saludvida.app.services.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.saludvida.app.model.dto.request.LoginRequestDTO;
import com.saludvida.app.model.dto.response.LoginResponseDTO;
import com.saludvida.app.services.IAuthService;

@Service
public class AuthServiceImpl extends ClientSupport implements IAuthService {

    public AuthServiceImpl(WebClient webClient) {
        super(webClient);
    }

    @Override
    public Optional<LoginResponseDTO> login(LoginRequestDTO request) {
        try {
            return Optional.ofNullable(post("/auth/login", request, LoginResponseDTO.class));
        } catch (WebClientResponseException.Unauthorized e) {
            return Optional.empty();
        } catch (WebClientResponseException.NotFound e) {
            return Optional.empty();
        } catch (WebClientResponseException.BadRequest e) {
            return Optional.empty();
        }
    }
}
