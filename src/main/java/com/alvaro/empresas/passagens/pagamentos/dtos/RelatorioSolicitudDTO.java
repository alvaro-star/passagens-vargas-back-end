package com.alvaro.empresas.passagens.pagamentos.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.UUID;

public record RelatorioSolicitudDTO(
        @NotNull
        UUID idEmpresa,
        @NotNull
        Date data
) {
}
