package com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions;

import lombok.Getter;

import java.util.HashMap;

@Getter
public class ValidationException extends RuntimeException {
    private final HashMap<String, Object> errors;
    private static final String defaultMessage = "Os dados enviados apresentam alguns erros";

    public ValidationException(HashMap<String, Object> errors) {
        super(defaultMessage);
        this.errors = errors;
    }

    public ValidationException(String campo, String mensagem) {
        super(defaultMessage);
        this.errors = new HashMap<>();
        this.errors.put(campo, mensagem);
    }
}
