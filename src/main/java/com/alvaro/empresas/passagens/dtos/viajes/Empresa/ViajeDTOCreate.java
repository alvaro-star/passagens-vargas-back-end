package com.alvaro.empresas.passagens.dtos.viajes.Empresa;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ViajeDTOCreate(
        @NotNull
        Integer idAutobus,

        @NotNull
        @Positive
        Integer plataforma,
        @NotNull
        @Future
        LocalDateTime fechaSalida,
        @NotNull
        Integer idLugarSalida,
        @NotNull
        Integer idLugarDestino,
        @Positive
        int horasViaje,

        @NotNull
        @DecimalMin(value = "10.0")
        BigDecimal precioPiso1,
        BigDecimal precioPiso2
) {
}
