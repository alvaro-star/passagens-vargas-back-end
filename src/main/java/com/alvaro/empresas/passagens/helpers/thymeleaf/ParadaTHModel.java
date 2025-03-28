package com.alvaro.empresas.passagens.helpers.thymeleaf;

import com.alvaro.empresas.passagens.paradas.models.ParadaModel;

public record ParadaTHModel(
        String cidade,
        String departamento,
        String lugar,
        String abreviacion) {
    public ParadaTHModel(ParadaModel model) {
        this(
                model.getLugar().getCidade().getNome(),
                model.getLugar().getCidade().getDepartamento().getNome(),
                model.getLugar().getNome(),
                model.getLugar().getCidade().getDepartamento().getAbreviacao());
    }
}
