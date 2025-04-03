package com.alvaro.empresas.passagens.dtos.viagens.seller;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ViagemCreateCopyDTO(
        @NotNull
        UUID idViagem,
        @NotNull
        @Future
        LocalDate dataNovo
) {
}
