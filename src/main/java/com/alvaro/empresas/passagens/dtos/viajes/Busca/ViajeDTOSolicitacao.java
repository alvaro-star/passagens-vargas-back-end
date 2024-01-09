package com.alvaro.empresas.passagens.dtos.viajes.Busca;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ViajeDTOSolicitacao(
        @NotNull
        Integer idLugarSalida,
        @NotNull
        Integer idLugarDestino,
        @NotNull
        @Future
        LocalDateTime fechaSalida
) {
}
