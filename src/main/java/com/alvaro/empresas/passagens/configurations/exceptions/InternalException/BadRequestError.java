package com.alvaro.empresas.passagens.configurations.exceptions.InternalException;

import com.alvaro.empresas.passagens.configurations.exceptions.StandardError;


public class BadRequestError extends StandardError {
    private String conteudo;

    public BadRequestError(Long timestamp, Integer status, String error, String message, String path) {
        super(timestamp, status, error, message, path);
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
}
