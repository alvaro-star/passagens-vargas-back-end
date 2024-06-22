package com.alvaro.empresas.passagens.dtos.viajes.Busca;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record ViajeDTOSolicitacaoFromAutobus(
        @NotNull
        Date dataAnalise,
        @NotNull
        Integer idAutobus,
        @NotEmpty
        String type
) {
}
