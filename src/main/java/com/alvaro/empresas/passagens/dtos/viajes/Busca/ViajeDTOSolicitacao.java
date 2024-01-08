package com.alvaro.empresas.passagens.dtos.viajes.Busca;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ViajeDTOSolicitacao(
        @NotNull
        Integer idSalida,
        @NotNull
        Integer idDestino,
        @NotNull
        @Future
        LocalDateTime fechaSalida
) {
}
