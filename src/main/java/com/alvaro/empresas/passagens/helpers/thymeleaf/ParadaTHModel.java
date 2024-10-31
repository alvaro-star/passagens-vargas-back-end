package com.alvaro.empresas.passagens.helpers.thymeleaf;

import com.alvaro.empresas.passagens.paradas.models.ParadaModel;

public record ParadaTHModel(
        String ciudad,
        String departamento,
        String lugar,
        String abreviacion
) {
    public ParadaTHModel(ParadaModel model) {
        this(
                model.getLugar().getCiudad().getNombre(),
                model.getLugar().getCiudad().getDepartamento().getNombre(),
                model.getLugar().getNombre(),
                model.getLugar().getCiudad().getDepartamento().getAbreviacion()
        );
    }
}
