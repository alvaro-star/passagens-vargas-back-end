package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.models.TrayectoModel;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TrayectoDTO(
        UUID codigo,
        @NotNull
        Integer idAutobus) {

    public TrayectoDTO(TrayectoModel model) {
        this(model.getCodigo(), null);
    }

    public TrayectoDTO(TrayectoModel model, Integer idAutobus) {
        this(model.getCodigo(), idAutobus);
    }
}
