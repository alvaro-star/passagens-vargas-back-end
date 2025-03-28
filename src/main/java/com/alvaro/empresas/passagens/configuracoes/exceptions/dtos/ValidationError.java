package com.alvaro.empresas.passagens.configuracoes.exceptions.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.util.HashMap;

@Data
@EqualsAndHashCode(callSuper = false)
public class ValidationError extends StandardError {
    private HashMap<String, String> errors = new HashMap<>();

    public ValidationError(Long timestamp, HttpStatus status, String message, String path) {
        super(timestamp, status, message, path);
    }

    public ValidationError(Long timestamp, HttpStatus status, String message, String path, HashMap<String, String> errors) {
        super(timestamp, status, message, path);
        this.errors = errors;
    }

    public void addError(String fieldName, String mensagem) {
        errors.put(fieldName, mensagem);
    }

    public void addError(FieldError error) {
        errors.put(error.getField(), error.getDefaultMessage());
    }
}
