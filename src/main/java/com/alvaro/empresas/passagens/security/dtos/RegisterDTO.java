package com.alvaro.empresas.passagens.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
        @Email(message = "Deve ser um email válido")
        @NotBlank
        String login,
        @NotBlank
        @Size(min = 8, max = 8, message = "Deve ter 8 números")
        String telefone,
        @NotBlank
        @Size(min = 8, message = "Deve ter um mínimo de 8 caracteres")
        String senha,
        @NotBlank
        @Size(min = 3, max = 50, message = "Deve ter entre 3 e 50 caracteres")
        String nome
) {
}