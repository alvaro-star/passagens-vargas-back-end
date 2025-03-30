package com.alvaro.empresas.passagens.paradas.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CidadeCreateDTO(
        @NotBlank
        String nome,
        @NotNull
        Integer idDepartamento
) {
}
