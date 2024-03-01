package com.alvaro.empresas.passagens.paradas.dtos;

import jakarta.validation.constraints.NotBlank;

public record LugarDtoUpdate(
        Integer id,
        @NotBlank
        String nombre) {
}
