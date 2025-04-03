package com.alvaro.empresas.passagens.dtos.viagens.seller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ViagemCreateDTO(
        @NotNull
        UUID idOnibus,
        @NotNull
        @Positive
        Integer plataforma,
        @NotNull
        @Future
        LocalDateTime dataSaida,
        @NotNull
        Integer idLugarSaida,
        @NotNull
        Integer idLugarDestino,
        @Positive
        int tempoViagem,

        @NotNull
        @DecimalMin(value = "10.0")
        BigDecimal precoPiso1,
        BigDecimal precoPiso2
) {
}
