package com.alvaro.empresas.passagens.configurations.exceptions;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final FieldMessage campo;

    public ValidationException(String mensaje) {
        super(mensaje);
        this.campo = new FieldMessage("unknow", mensaje);
    }

    public ValidationException(String campo, String mensaje) {
        super(mensaje);
        this.campo = new FieldMessage(campo, mensaje);
    }

    public ValidationException(FieldMessage campo) {
        super(campo.getMessage());
        this.campo = campo;
    }
}
