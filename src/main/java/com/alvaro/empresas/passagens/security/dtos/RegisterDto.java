package com.alvaro.empresas.passagens.security.dtos;

import jakarta.validation.constraints.NotBlank;

public record RegisterDto(
        @NotBlank(message = "El numero de carnet")
        String login,
        @NotBlank
        String carnet,
        @NotBlank
        String contrasena,
        @NotBlank
        String nombre,
        @NotBlank
        String role
) {
}
