package com.alvaro.empresas.passagens.dtos.viajes.Busca;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ViajeDTOSolicitacao(
        @NotNull
        Integer idLugarSalida,
        @NotNull
        Integer idLugarDestino,
        @NotNull
        @FutureOrPresent
        LocalDate fechaSalida
) {
}
