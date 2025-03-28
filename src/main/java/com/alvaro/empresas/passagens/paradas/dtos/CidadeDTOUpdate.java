package com.alvaro.empresas.passagens.paradas.dtos;

import jakarta.validation.constraints.NotBlank;

public record CidadeDTOUpdate(
        Integer id,
        @NotBlank
        String nome
) {

}
