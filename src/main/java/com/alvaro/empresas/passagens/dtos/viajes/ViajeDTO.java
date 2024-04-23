package com.alvaro.empresas.passagens.dtos.viajes;

import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaViajeFormDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ViajeDTO(
        UUID codigo,
        @NotNull
        Integer idAutobus,
        @NotNull @Valid
        ParadaViajeFormDTO salida,
        @NotNull @Valid
        ParadaViajeFormDTO destino,
        @NotNull
        Float precioPiso1,
        Float precioPiso2
) {
}
