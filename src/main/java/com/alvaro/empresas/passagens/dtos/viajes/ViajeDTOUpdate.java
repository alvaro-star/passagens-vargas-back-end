package com.alvaro.empresas.passagens.dtos.viajes;

import jakarta.validation.constraints.NotNull;

public record ViajeDTOUpdate(
        @NotNull
        Integer idAutobus
) {
}
