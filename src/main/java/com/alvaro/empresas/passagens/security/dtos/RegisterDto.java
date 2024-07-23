package com.alvaro.empresas.passagens.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDto(
        @Email
        @NotBlank
        String login,
        @NotBlank
        @Size(min = 8, max = 8, message = "Debe tener 8 numeros")
        String telefono,
        @NotBlank
        @Size(min = 8)
        String contrasena,
        @NotBlank
        @Size(min = 3, max = 50)
        String nombre
) {
}
