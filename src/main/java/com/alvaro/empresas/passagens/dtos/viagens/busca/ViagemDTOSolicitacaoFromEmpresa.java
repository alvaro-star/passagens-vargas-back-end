package com.alvaro.empresas.passagens.dtos.viagens.busca;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record ViagemDTOSolicitacaoFromEmpresa(
        @NotNull
        LocalDate dataAnalise,
        @NotNull
        UUID idEmpresa
) {
}
