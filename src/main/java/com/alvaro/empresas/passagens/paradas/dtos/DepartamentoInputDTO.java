package com.alvaro.empresas.passagens.paradas.dtos;

import jakarta.validation.constraints.NotBlank;

public record DepartamentoInputDTO(
        @NotBlank
        String nome,
        @NotBlank
        String abreviacao
) {
}