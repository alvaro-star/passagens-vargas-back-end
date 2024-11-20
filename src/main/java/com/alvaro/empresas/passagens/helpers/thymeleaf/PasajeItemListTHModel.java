package com.alvaro.empresas.passagens.helpers.thymeleaf;

import com.alvaro.empresas.passagens.models.PasajeModel;

public record PasajeItemListTHModel(
        Integer nsilla,
        String carnet,
        String nombre,
        String nascimiento,
        ParadaTHModel origen,
        ParadaTHModel destino
) {
    public PasajeItemListTHModel(PasajeModel model) {
        this(
                model.getNSilla(),
                model.getCarnet(),
                model.getNombre(),
                model.getNacimentoString(),
                new ParadaTHModel(model.getSalida()),
                new ParadaTHModel(model.getDestino())
        );
    }
}
