package com.alvaro.empresas.passagens.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioDTOUpdate(
        @Email
        String email,
        @NotBlank
        String senha,
        @NotNull
        String telefone,
        @NotNull
        String nome
) {
}
