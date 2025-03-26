package com.alvaro.empresas.passagens.helpers.thymeleaf;

import com.alvaro.empresas.passagens.models.PassagemModel;

public record PasajeItemListTHModel(
        Integer nsilla,
        String carnet,
        String nombre,
        String nascimiento,
        ParadaTHModel origen,
        ParadaTHModel destino
) {
    public PasajeItemListTHModel(PassagemModel model) {
        this(
                model.getNumeroAssento(),
                model.getDocumento(),
                model.getNome(),
                model.getNascimentoString(),
                new ParadaTHModel(model.getSaida()),
                new ParadaTHModel(model.getDestino())
        );
    }
}
