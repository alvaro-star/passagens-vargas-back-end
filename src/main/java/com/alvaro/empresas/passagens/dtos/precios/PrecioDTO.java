package com.alvaro.empresas.passagens.dtos.precios;

import com.alvaro.empresas.passagens.models.PrecioModel;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PrecioDTO(
        UUID id,
        @NotNull
        BigDecimal precio,
        @NotNull
        Integer nPiso,
        @NotNull
        Boolean lleno,
        @NotNull
        Integer nSillasDisponibles,
        @NotNull
        UUID idViaje
) {
    public PrecioDTO(PrecioModel model, UUID idViaje) {
        this(model.getId(), model.getPrecio(), model.getNPiso(), model.getLleno(), model.getNSillasDisponibles(), idViaje);
    }

    public PrecioDTO(PrecioModel model) {
        this(model.getId(), model.getPrecio(), model.getNPiso(), model.getLleno(), model.getNSillasDisponibles(), null);
    }
}
