package com.alvaro.empresas.passagens.dtos.viajes;

import com.alvaro.empresas.passagens.models.ViajeModel;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ViajeDTOList(
        Integer id,
        @NotNull
        UUID idTrayecto,
        @NotNull
        Integer salida,
        @NotNull
        Integer destino
) {
    public ViajeDTOList(ViajeModel model, UUID idTrayecto, Integer salida, Integer destino) {
        this(model.getId(), idTrayecto, salida, destino);
    }
}
