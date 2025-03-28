package com.alvaro.empresas.passagens.paradas.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record ParadaViagemFormDTO(
        Integer id,
        @NotNull
        @Positive
        Integer plataforma,
        @NotNull
        @Future
        LocalDateTime dataHora,
        @NotNull
        Integer idLugar
) {
}
