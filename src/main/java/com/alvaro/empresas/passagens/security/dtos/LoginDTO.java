package com.alvaro.empresas.passagens.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginDTO(
        @NotBlank
        @Email(message = "Deve ser um email válido")
        String email,
        @NotBlank
        @Size(min = 8, message = "Deve ter um mínimo de 8 caracteres")
        String senha
) {

}