package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.models.PrecioModel;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PrecioDTO(
        UUID id,
        @NotNull
        Float precio,
        @NotNull
        Integer nPiso
) {
    public PrecioDTO(PrecioModel model) {
        this(model.getId(), model.getPrecio(), model.getNPiso());
    }
}
