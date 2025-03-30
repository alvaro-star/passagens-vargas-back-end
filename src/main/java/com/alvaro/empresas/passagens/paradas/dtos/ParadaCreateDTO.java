package com.alvaro.empresas.passagens.paradas.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.UUID;

public record ParadaCreateDTO(
        @NotNull
        @Positive
        Integer plataforma,
        @NotNull
        @Future
        LocalDateTime dataHora,
        @NotNull
        Integer idLugar,
        @NotNull
        UUID idViagem
) {
}