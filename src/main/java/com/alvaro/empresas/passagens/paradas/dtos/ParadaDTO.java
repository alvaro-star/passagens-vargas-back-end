package com.alvaro.empresas.passagens.paradas.dtos;

import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.UUID;

public record ParadaDTO(
        Integer id,
        @NotNull
        @Positive
        Integer plataforma,
        @NotNull
        @Future
        LocalDateTime dataHora,
        @NotNull
        Integer idLugar,
        @NotNull
        UUID idViaje
) {

    public ParadaDTO(ParadaModel model) {
        this(model.getId(), model.getPlataforma(), model.getDataHora(), model.getLugarId(), model.getEmpresaId());
    }
}
