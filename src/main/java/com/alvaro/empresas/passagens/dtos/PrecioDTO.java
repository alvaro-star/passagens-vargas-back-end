package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.models.PrecioModel;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PrecioDTO(
        UUID id,
        @NotNull
        Float precio,
        @NotNull
        Integer nPiso,
        @NotNull
        Boolean lleno,
        @NotNull
        Integer nSillasDisponibles,
        @NotNull
        Integer idViaje
) {
    public PrecioDTO(PrecioModel model, Integer idViaje) {
        this(model.getId(), model.getPrecio(), model.getNPiso(), model.getLleno(), model.getNSillasDisponibles(), idViaje);
    }

    public PrecioDTO(PrecioModel model) {
        this(model.getId(), model.getPrecio(), model.getNPiso(), model.getLleno(), model.getNSillasDisponibles(), null);
    }
}
