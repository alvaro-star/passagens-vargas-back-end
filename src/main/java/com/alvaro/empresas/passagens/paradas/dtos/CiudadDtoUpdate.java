package com.alvaro.empresas.passagens.paradas.dtos;

import jakarta.validation.constraints.NotBlank;

public record CiudadDtoUpdate(
        Integer id,
        @NotBlank
        String nombre
) {

}
