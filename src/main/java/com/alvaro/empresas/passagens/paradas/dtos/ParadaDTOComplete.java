package com.alvaro.empresas.passagens.paradas.dtos;

import com.alvaro.empresas.passagens.paradas.models.ParadaModel;

import java.time.LocalDateTime;
import java.util.UUID;

public record ParadaDTOComplete(
        Integer id,
        LocalDateTime dataHora,
        Integer plataforma,
        UUID idTrayecto,
        Integer idLugar,
        String lugar,
        String ciudad,
        String departamento,
        String abreviacion
) {
    public ParadaDTOComplete(ParadaModel model, UUID idTrayecto) {
        this(model.getId(),
                model.getDataHora(),
                model.getPlataforma(),
                idTrayecto,
                model.getLugar().getId(),
                model.getLugar().getNombre(),
                model.getLugar().getCiudad().getNombre(),
                model.getLugar().getCiudad().getDepartamento().getNombre(),
                model.getLugar().getCiudad().getDepartamento().getAbreviacion());
    }
}