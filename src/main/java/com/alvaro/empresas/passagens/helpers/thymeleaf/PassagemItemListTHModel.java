package com.alvaro.empresas.passagens.helpers.thymeleaf;

import com.alvaro.empresas.passagens.models.PassagemModel;

public record PassagemItemListTHModel(
        Integer nAssento,
        String cpf,
        String nome,
        String nascimento,
        ParadaTHModel saida,
        ParadaTHModel destino) {
    public PassagemItemListTHModel(PassagemModel model) {
        this(
                model.getNAssento(),
                model.getCpf(),
                model.getNome(),
                model.getNascimentoString(),
                new ParadaTHModel(model.getSaida()),
                new ParadaTHModel(model.getDestino()));
    }
}
