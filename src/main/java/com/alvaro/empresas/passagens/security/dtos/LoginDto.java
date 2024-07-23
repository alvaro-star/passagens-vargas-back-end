package com.alvaro.empresas.passagens.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginDto(
        @NotBlank
        @Email
        String login,
        @NotBlank
        @Size(min = 8)
        String contrasena
) {

}