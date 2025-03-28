package com.alvaro.empresas.passagens.paradas.dtos;

import jakarta.validation.constraints.NotBlank;

public record LugarDTOUpdate(
        Integer id,
        @NotBlank
        String nome) {
}
