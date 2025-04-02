package com.alvaro.empresas.passagens.helpers.thymeleaf;

import com.alvaro.empresas.passagens.models.PassagemModel;

public record PassagemItemListTHModel(
        Integer nsilla,
        String carnet,
        String nome,
        String nascimiento,
        ParadaTHModel origen,
        ParadaTHModel destino) {
    public PassagemItemListTHModel(PassagemModel model) {
        this(
                model.getNAssento(),
                model.getDocumento(),
                model.getNome(),
                model.getNascimentoString(),
                new ParadaTHModel(model.getSaida()),
                new ParadaTHModel(model.getDestino()));
    }
}
