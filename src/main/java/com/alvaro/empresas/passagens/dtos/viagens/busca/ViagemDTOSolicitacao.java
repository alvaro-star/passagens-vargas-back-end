package com.alvaro.empresas.passagens.dtos.viagens.busca;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ViagemDTOSolicitacao(
        @NotNull
        Integer idCidadeSaida,
        @NotNull
        Integer idCidadeDestino,
        @NotNull
        @FutureOrPresent
        LocalDate dataSaida
) {
}
