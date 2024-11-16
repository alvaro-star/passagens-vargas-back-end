package com.alvaro.empresas.passagens.dtos.viajes;

import com.alvaro.empresas.passagens.models.ViajeModel;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ViajeDTOUpdate(
        UUID codigo,
        @NotNull
        Integer idAutobus
) {
    public ViajeDTOUpdate(ViajeModel model) {
        this(model.getCodigo(), model.getAutobusId());
    }
}
