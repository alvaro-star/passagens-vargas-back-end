package com.alvaro.empresas.passagens.paradas.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ParadaUpdateDTO(
        @NotNull
        @Future
        LocalDateTime dataHora,
        @NotNull
        Integer plataforma,
        Integer idLugar
) {
}