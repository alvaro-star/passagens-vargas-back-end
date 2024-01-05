package com.alvaro.empresas.passagens.autobuses.dtos.pisos;

import com.alvaro.empresas.passagens.autobuses.models.PosicionIndisponibleModel;
import jakarta.validation.constraints.NotNull;

public record PosicionIndisponibleDTO(
        Integer id,
        @NotNull
        Integer numero) {

    public PosicionIndisponibleDTO(PosicionIndisponibleModel model) {
        this(model.getId(), model.getNumero());
    }
}
