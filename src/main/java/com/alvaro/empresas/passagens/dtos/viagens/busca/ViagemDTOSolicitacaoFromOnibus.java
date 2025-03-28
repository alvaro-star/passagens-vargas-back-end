package com.alvaro.empresas.passagens.dtos.viagens.busca;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ViagemDTOSolicitacaoFromOnibus(
        @NotNull
        LocalDate dataAnalise,
        @NotNull
        UUID idOnibus
) {
}
