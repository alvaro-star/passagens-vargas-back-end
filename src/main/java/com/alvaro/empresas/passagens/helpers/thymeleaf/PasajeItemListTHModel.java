package com.alvaro.empresas.passagens.helpers.thymeleaf;

import com.alvaro.empresas.passagens.models.PasajeModel;
import org.thymeleaf.context.Context;

public record PasajeItemListTHModel(
        Integer nsilla,
        String carnet,
        String nombre,
        String nascimiento,
        ParadaTHModel salida,
        ParadaTHModel destino
) {
    public PasajeItemListTHModel(PasajeModel model) {
        this(
                model.getNSilla(),
                model.getCarnet(),
                model.getNombre(),
                model.getNascimento().toString(),
                new ParadaTHModel(model.getSalida()),
                new ParadaTHModel(model.getDestino())
        );
    }
}
