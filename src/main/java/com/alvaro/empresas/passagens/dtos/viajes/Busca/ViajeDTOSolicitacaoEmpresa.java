package com.alvaro.empresas.passagens.dtos.viajes.Busca;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ViajeDTOSolicitacaoEmpresa(
        @NotNull
        Integer idCiudadSalida,
        Integer idCiudadDestino,
        @NotNull
        LocalDate fechaSalida
) {
}
