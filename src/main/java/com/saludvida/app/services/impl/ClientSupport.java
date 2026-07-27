package com.saludvida.app.services.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

abstract class ClientSupport {
    protected final WebClient webClient;

    protected ClientSupport(WebClient webClient) {
        this.webClient = webClient;
    }

    protected <T> List<T> getList(String uri, ParameterizedTypeReference<List<T>> type) {
        try {
            var result = webClient.get().uri(uri).retrieve().bodyToMono(type).block();
            return result == null ? List.of() : result;
        } catch (WebClientResponseException.NotFound e) {
            return List.of();
        }
    }

    protected <T> Optional<T> getOne(String uri, Class<T> type) {
        try {
            return Optional.ofNullable(webClient.get().uri(uri).retrieve().bodyToMono(type).block());
        } catch (WebClientResponseException.NotFound e) {
            return Optional.empty();
        }
    }

    protected <T, R> T post(String uri, R body, Class<T> type) {
        return webClient.post().uri(uri).bodyValue(body).retrieve().bodyToMono(type).block();
    }

    protected <T, R> T put(String uri, R body, Class<T> type) {
        return webClient.put().uri(uri).bodyValue(body).retrieve().bodyToMono(type).block();
    }

    protected void patch(String uri) {
        webClient.patch().uri(uri).retrieve().toBodilessEntity().block();
    }
}
