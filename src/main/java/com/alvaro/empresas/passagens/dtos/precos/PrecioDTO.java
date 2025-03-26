package com.alvaro.empresas.passagens.dtos.precos;

import com.alvaro.empresas.passagens.models.PrecoModel;
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
    public PrecioDTO(PrecoModel model, UUID idViaje) {
        this(model.getId(), model.getPrecio(), model.getNPiso(), model.getLleno(), model.getNSillasDisponibles(), idViaje);
    }

    public PrecioDTO(PrecoModel model) {
        this(model.getId(), model.getPrecio(), model.getNPiso(), model.getLleno(), model.getNSillasDisponibles(), model.getViagemId());
    }
}
