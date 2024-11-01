package com.alvaro.empresas.passagens.dtos;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record EmpresaDTO(
        UUID id,
        @NotBlank
        String nombre,
        @NotBlank
        String logo,
        @NotBlank
        String numeroCuenta
) {
}
