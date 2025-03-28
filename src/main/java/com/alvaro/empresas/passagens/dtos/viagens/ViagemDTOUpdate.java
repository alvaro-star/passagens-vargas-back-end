package com.alvaro.empresas.passagens.dtos.viagens;

import com.alvaro.empresas.passagens.models.ViagemModel;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ViagemDTOUpdate(
        UUID idViagem,
        @NotNull
        Integer idOnibus
) {
    public ViagemDTOUpdate(ViagemModel model) {
        this(model.getId(), model.getOnibusId());
    }
}
