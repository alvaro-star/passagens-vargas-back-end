package com.alvaro.empresas.passagens.dtos.viajes;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ViajesSolicitacao(
        @NotNull
        Integer idSalida,
        @NotNull
        Integer idDestino,
        @NotNull
        LocalDateTime fechaSalida
) {
}
