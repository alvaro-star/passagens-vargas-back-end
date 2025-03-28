package com.alvaro.empresas.passagens.security.dtos;

public record TokenDTO(
        String accessToken,
        String refreshToken
) {
}
