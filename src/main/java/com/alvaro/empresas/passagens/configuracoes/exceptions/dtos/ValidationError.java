package com.alvaro.empresas.passagens.configuracoes.exceptions.dtos;

import java.util.ArrayList;
import java.util.List;

import com.alvaro.empresas.passagens.configuracoes.exceptions.FieldMessage;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ValidationError extends StandardError {
    private static final long serialVersionUID = 1L;

    private List<FieldMessage> errors = new ArrayList<>();

    public ValidationError(Long timestamp, HttpStatus status, String message, String path) {
        super(timestamp, status, message, path);
    }

    public ValidationError(Long timestamp, HttpStatus status, String message, String path, List<FieldMessage> errors) {
        super(timestamp, status, message, path);
        this.errors = errors;
    }

    public void addError(String fieldName, String mensagem) {
        errors.add(new FieldMessage(fieldName, mensagem));
    }

    public void addError(FieldError error) {
        errors.add(new FieldMessage(error));
    }
}
