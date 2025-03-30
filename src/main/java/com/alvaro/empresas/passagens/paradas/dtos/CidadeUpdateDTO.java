package com.alvaro.empresas.passagens.paradas.dtos;

import jakarta.validation.constraints.NotBlank;

public record CidadeUpdateDTO(
        @NotBlank
        String nome
) {

}
