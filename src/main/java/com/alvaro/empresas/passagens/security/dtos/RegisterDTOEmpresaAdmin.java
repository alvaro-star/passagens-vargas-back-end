package com.alvaro.empresas.passagens.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RegisterDTOEmpresaAdmin(
        @Email
        @NotBlank
        String email,
        @NotNull
        UUID idEmpresa
) {
}
