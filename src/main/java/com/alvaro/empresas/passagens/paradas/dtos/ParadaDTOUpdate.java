package com.alvaro.empresas.passagens.paradas.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public record ParadaDTOUpdate(
        Integer id,
        @NotNull
        LocalDateTime dataHora,
        @NotNull
        Integer idLugar) {
}
