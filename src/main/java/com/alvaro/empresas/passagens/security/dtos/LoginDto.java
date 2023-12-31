package com.alvaro.empresas.passagens.security.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginDto(
        @NotBlank(message = "El login no puede estar vacio")
        String login,
        @NotBlank(message = "El login no puede estar vacio")
        String contrasena
) {

}