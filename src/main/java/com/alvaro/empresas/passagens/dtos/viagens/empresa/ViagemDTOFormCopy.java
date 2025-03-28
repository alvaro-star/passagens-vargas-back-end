package com.alvaro.empresas.passagens.dtos.viagens.empresa;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ViagemDTOFormCopy(
        @NotNull
        UUID idViagem,
        @NotNull
        @Future
        LocalDate dataNovo
) {
}
