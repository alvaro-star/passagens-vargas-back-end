package com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions;

import com.alvaro.empresas.passagens.configuracoes.exceptions.dtos.FieldMessage;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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
