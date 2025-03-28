package com.alvaro.empresas.passagens.dtos;

import jakarta.validation.constraints.NotBlank;

public record InputEmpresaDTO(
        @NotBlank
        String nome,
        @NotBlank
        String logo,
        @NotBlank
        String nConta
) {
}
