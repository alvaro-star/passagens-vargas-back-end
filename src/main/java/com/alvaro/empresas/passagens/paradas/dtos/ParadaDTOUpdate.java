package com.alvaro.empresas.passagens.paradas.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ParadaDTOUpdate(
        Integer id,
        @NotNull
        @Future
        LocalDateTime dataHora,
        Integer plataforma,
        Integer idLugar) {
}
