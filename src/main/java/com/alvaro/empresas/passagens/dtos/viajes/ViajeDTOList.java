package com.alvaro.empresas.passagens.dtos.viajes;

import com.alvaro.empresas.passagens.models.ViajeModel;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ViajeDTOList(
        Integer id,
        @NotNull
        Integer plataforma,
        @NotNull
        UUID idTrayecto,
        @NotNull
        Integer salida,
        @NotNull
        Integer destino
) {
    public ViajeDTOList(ViajeModel model, UUID idTrayecto, Integer salida, Integer destino) {
        this(model.getId(), model.getPlataforma(), idTrayecto, salida, destino);
    }
}
