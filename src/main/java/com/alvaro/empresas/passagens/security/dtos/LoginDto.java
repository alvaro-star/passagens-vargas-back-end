package com.alvaro.empresas.passagens.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginDto(
        @NotBlank
        @Email(message = "Debe ser un email valido")
        String login,
        @NotBlank
        @Size(min = 8, message = "Debe tener un minimo de 8 caracteres")
        String contrasena
) {

}