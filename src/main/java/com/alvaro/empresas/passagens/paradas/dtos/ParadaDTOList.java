package com.alvaro.empresas.passagens.paradas.dtos;

import com.alvaro.empresas.passagens.paradas.models.ParadaModel;

import java.time.LocalDateTime;

public record ParadaDTOList(
        Integer id,
        LocalDateTime dataHora,
        String lugar,
        String ciudad,
        String departamento
) {
    public ParadaDTOList(ParadaModel model, String lugar, String ciudad, String departamento) {
        this(model.getId(), model.getDataHora(), lugar, ciudad, departamento);
    }
}