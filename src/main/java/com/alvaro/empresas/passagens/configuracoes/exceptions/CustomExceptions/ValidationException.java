package com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions;

import java.util.ArrayList;
import java.util.List;

import com.alvaro.empresas.passagens.configuracoes.exceptions.FieldMessage;
import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final List<FieldMessage> errors;
    private static final String defaultMessage = "Os dados enviados apresentam alguns erros";

    public ValidationException(String mensaje) {
        super(defaultMessage);
        this.errors = new ArrayList<>();
        this.errors.add(new FieldMessage("unknow", mensaje));
    }

    public ValidationException(List<FieldMessage> errors) {
        super(defaultMessage);
        this.errors = errors;
    }

    public ValidationException(String campo, String mensaje) {
        super(defaultMessage);
        this.errors = new ArrayList<>();
        this.errors.add(new FieldMessage(campo, mensaje));
    }
}
