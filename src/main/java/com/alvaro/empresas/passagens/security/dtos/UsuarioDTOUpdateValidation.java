package com.alvaro.empresas.passagens.security.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UsuarioDTOUpdateValidation(
        @NotNull
        UUID codigo
) {
}
