package com.alvaro.empresas.passagens.dtos.viajes;

import com.alvaro.empresas.passagens.models.ViajeModel;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ViajeDTOList(
        UUID codigo,
        @NotNull
        Integer idAutobus
) {
    public ViajeDTOList(ViajeModel model, Integer idAutobus) {
        this(model.getCodigo(), idAutobus);
    }
}
