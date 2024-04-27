package com.alvaro.empresas.passagens.dtos.viajes;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ViajeDTOUpdate(
        UUID codigo,
        @NotNull
        Integer idAutobus
) {
}
