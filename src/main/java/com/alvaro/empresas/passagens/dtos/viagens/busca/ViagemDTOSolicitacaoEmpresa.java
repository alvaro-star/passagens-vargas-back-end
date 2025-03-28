package com.alvaro.empresas.passagens.dtos.viagens.busca;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record ViagemDTOSolicitacaoEmpresa(
        @NotNull
        Integer idCidadeSaida,
        Integer idCidadeDestino,
        @NotNull
        LocalDate dataSaida
) {
}
