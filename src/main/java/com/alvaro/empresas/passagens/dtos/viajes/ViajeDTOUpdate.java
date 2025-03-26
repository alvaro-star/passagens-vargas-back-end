package com.alvaro.empresas.passagens.dtos.viajes;

import com.alvaro.empresas.passagens.models.ViagemModel;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ViajeDTOUpdate(
        UUID codigo,
        @NotNull
        Integer idAutobus
) {
    public ViajeDTOUpdate(ViagemModel model) {
        this(model.getId(), model.getAutobusId());
    }
}
