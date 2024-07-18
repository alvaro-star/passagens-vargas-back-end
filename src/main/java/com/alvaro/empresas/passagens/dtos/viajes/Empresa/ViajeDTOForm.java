package com.alvaro.empresas.passagens.dtos.viajes.Empresa;

import com.alvaro.empresas.passagens.paradas.dtos.ParadaViajeFormDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ViajeDTOForm(
        UUID codigo,
        @NotNull
        Integer idAutobus,
        @NotNull @Valid
        ParadaViajeFormDTO salida,
        @NotNull @Valid
        ParadaViajeFormDTO destino,
        @NotNull
        @DecimalMin(value = "10.0")
        BigDecimal precioPiso1,
        BigDecimal precioPiso2
) {
}
