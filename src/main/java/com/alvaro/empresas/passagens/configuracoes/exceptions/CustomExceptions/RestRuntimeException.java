package com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class RestRuntimeException extends RuntimeException {
    private final HttpStatus status;

    public RestRuntimeException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public RestRuntimeException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }
}
