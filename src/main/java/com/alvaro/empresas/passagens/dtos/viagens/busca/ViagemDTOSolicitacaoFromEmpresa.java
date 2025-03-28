package com.alvaro.empresas.passagens.dtos.viagens.busca;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public record ViagemDTOSolicitacaoFromEmpresa(
        @NotNull
        LocalDate dataAnalise,
        @NotNull
        UUID idEmpresa
) {
}
