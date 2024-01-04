package com.alvaro.empresas.passagens.configurations.exceptions;

public class ValidationException extends RuntimeException {
    public ValidationException(String mensaje) {
        super(mensaje);
    }
}
