package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.models.EmpresaModel;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record EmpresaDto(
        UUID id,
        @NotBlank
        String nombre,
        @NotBlank
        String logo,
        @NotBlank
        String numeroCuenta
) {
}
