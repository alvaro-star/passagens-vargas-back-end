package com.alvaro.empresas.passagens.paradas.dtos;

import com.alvaro.empresas.passagens.paradas.models.ParadaModel;

import java.time.LocalDateTime;
import java.util.UUID;

public record ParadaDTOComplete(
        Integer id,
        LocalDateTime dataHora,
        Integer plataforma,
        String tipo,
        UUID idViagem,
        Integer idLugar,
        String lugar,
        String cidade,
        String departamento,
        String abreviacao
) {
    public ParadaDTOComplete(ParadaModel model) {
        this(model.getId(),
                model.getDataHora(),
                model.getPlataforma(),
                model.getTipo().toString(),
                model.getViagemId(),
                model.getLugarId(),
                model.getLugar().getNome(),
                model.getLugar().getCidade().getNome(),
                model.getLugar().getCidade().getDepartamento().getNome(),
                model.getLugar().getCidade().getDepartamento().getAbreviacao());
    }
}