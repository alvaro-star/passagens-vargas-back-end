package com.alvaro.empresas.passagens.dtos.viajes;

import com.alvaro.empresas.passagens.models.ViajeModel;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ViajeDTO(
        Integer id,
        @NotNull
        UUID idTrayecto,
        @NotNull
        Float precioPiso1,
        Float precioPiso2
) {
    public ViajeDTO(ViajeModel model, UUID idTrayecto, Float precio1, Float precio2) {
        this(model.getId(), idTrayecto, precio1, precio2);
    }

}
