package com.alvaro.empresas.passagens.services;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HashMetodoPagamentoValor {
    private String nome;
    protected Double valor;

    public HashMetodoPagamentoValor(String nome, Double valor) {
        this.nome = nome;
        this.valor = valor;
    }
}
