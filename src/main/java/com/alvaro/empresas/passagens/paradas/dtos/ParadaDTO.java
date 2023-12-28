package com.alvaro.empresas.passagens.paradas.dtos;

import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ParadaDTO(
        Integer id,
        @NotNull
        LocalDateTime dataHora,
        @NotNull
        Integer idLugar,
        @NotNull
        UUID idTrayecto
) {

    public ParadaDTO(ParadaModel model, int idLugar, UUID idTrayecto) {
        this(model.getId(), model.getDataHora(), idLugar, idTrayecto);
    }
}
