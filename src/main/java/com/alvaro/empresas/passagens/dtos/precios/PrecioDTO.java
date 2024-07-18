package com.alvaro.empresas.passagens.dtos.precios;

import com.alvaro.empresas.passagens.models.PrecioModel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record PrecioDTO(
        UUID id,
        @NotNull
        @DecimalMin(value = "10.00")
        BigDecimal precio,
        @NotNull
        @Positive
        Integer nPiso,
        @NotNull
        Boolean lleno,
        @NotNull
        @Positive
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
