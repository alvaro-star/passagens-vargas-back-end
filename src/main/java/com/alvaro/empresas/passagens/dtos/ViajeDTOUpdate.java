package com.alvaro.empresas.passagens.dtos;

import jakarta.validation.constraints.NotNull;

public record ViajeDTOUpdate(
        @NotNull
        Integer plataforma,
        Integer salida,
        Integer destino
) {
}
