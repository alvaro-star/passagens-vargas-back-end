package com.alvaro.empresas.passagens.paradas.dtos;

import com.alvaro.empresas.passagens.paradas.models.ParadaModel;

import java.time.LocalDateTime;
import java.util.UUID;

public record ParadaResponseDTO(
        Integer id,
        LocalDateTime dataHora,
        Integer plataforma,
        String tipo,
        UUID idViagem,
        LugarResponseDTO lugar
) {
    public ParadaResponseDTO(ParadaModel model) {
        this(model.getId(),
                model.getDataHora(),
                model.getPlataforma(),
                model.getTipo().toString(),
                model.getViagemId(),
                new LugarResponseDTO(model.getLugar())
        );
    }
}