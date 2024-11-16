package com.alvaro.empresas.passagens.dtos.viajes.Empresa;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ViajeDTOFormCopy(
        @NotNull
        UUID idViaje,
        @NotNull
        @Future
        LocalDate dataNovo
) {
}
