package com.alvaro.empresas.passagens.dtos.viajes.Busca;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record ViajeDTOSolicitacaoEmpresa(
        @NotNull
        Integer idCiudadSalida,
        Integer idCiudadDestino,
        @NotNull
        LocalDate fechaSalida
) {
}
