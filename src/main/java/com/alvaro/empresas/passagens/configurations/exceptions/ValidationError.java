package com.alvaro.empresas.passagens.configurations.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class ValidationError extends StandardError {
    private static final long serialVersionUID = 1L;

    private List<FieldMessage> errors = new ArrayList<>();

    public ValidationError(Long timestamp, Integer status, String error, String message, String path) {
        super(timestamp, status, error, message, path);
    }

    public void addError(String fieldName, String mensagem) {
        errors.add(new FieldMessage(fieldName, mensagem));
    }

    public void addError(FieldError error) {
        errors.add(new FieldMessage(error));
    }
}
