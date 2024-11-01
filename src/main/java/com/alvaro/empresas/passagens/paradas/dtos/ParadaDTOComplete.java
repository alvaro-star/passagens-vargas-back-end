package com.alvaro.empresas.passagens.paradas.dtos;

import com.alvaro.empresas.passagens.paradas.models.ParadaModel;

import java.time.LocalDateTime;
import java.util.UUID;

public record ParadaDTOComplete(
        Integer id,
        LocalDateTime dataHora,
        Integer plataforma,
        String tipo,
        UUID idViaje,
        Integer idLugar,
        String lugar,
        String ciudad,
        String departamento,
        String abreviacion
) {
    public ParadaDTOComplete(ParadaModel model) {
        this(model.getId(),
                model.getDataHora(),
                model.getPlataforma(),
                model.getTipo().toString(),
                model.getViajeCodigo(),
                model.getLugarId(),
                model.getLugar().getNombre(),
                model.getLugar().getCiudad().getNombre(),
                model.getLugar().getCiudad().getDepartamento().getNombre(),
                model.getLugar().getCiudad().getDepartamento().getAbreviacion());
    }
}