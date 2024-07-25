package com.alvaro.empresas.passagens.dtos.viajes.Busca;

import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.UUID;

public record ViajeDTOSolicitacaoFromEmpresa(
        @NotNull
        Date dataAnalise,
        @NotNull
        UUID idEmpresa
) {
}
