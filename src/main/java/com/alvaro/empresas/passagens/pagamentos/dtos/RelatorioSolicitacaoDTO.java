package com.alvaro.empresas.passagens.pagamentos.dtos;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record RelatorioSolicitacaoDTO(
        @NotNull
        UUID idEmpresa,
        @NotNull
        LocalDate data
) {
}
