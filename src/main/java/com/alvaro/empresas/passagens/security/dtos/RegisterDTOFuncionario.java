package com.alvaro.empresas.passagens.security.dtos;

import jakarta.validation.constraints.NotBlank;

public record RegisterDTOFuncionario(
        @NotBlank
        String email
) {
}
