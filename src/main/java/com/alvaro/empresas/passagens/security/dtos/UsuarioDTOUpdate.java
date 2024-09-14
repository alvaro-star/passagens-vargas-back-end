package com.alvaro.empresas.passagens.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UsuarioDTOUpdate(
        @Email
        String email,
        @NotNull
        String contrasena,
        @NotNull
        String telefone,
        @NotNull
        String nombre
) {
}
