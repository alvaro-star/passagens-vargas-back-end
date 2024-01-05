package com.alvaro.empresas.passagens.configurations.exceptions;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private FieldMessage campo;

    public ValidationException(String mensaje) {
        super(mensaje);
        campo.setName("unknow");
    }

    public ValidationException(FieldMessage campo) {
        super(campo.getMessage());
        this.campo = campo;
    }
}
