package com.alvaro.empresas.passagens.configuracoes.exceptions.dtos;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import lombok.Getter;

@Getter
public class ValidationError extends StandardError {
    private HashMap<String, Object> errors;

    public ValidationError(Long timestamp, HttpStatus status, String message, String path, List<FieldError> errors) {
        super(timestamp, status, message, path);
        setErrors(errors);
    }

    public ValidationError(Long timestamp, HttpStatus status, String message, String path,
            HashMap<String, Object> errors) {
        super(timestamp, status, message, path);
        this.errors = errors;
    }

    private void setErrors(List<FieldError> errors) {
        this.errors = groupErrorsByFieldName(errors);
    }

    private HashMap<String, Object> groupErrorsByFieldName(List<FieldError> fieldErrors) {
        HashMap<String, Object> errors = new HashMap<>();
        for (FieldError fieldError : fieldErrors) {
            String[] fieldPath = fieldError.getField().split("\\.");
            HashMap<String, Object> current = errors;
            boolean breaked = false;
            for (int i = 0; i < fieldPath.length - 1; i++) {
                String key = fieldPath[i];

                @SuppressWarnings("unchecked")
                HashMap<String, Object> nestedMap = (HashMap<String, Object>) current.computeIfAbsent(key,
                        k -> new HashMap<String, Object>());

                if (!(nestedMap instanceof HashMap<String, Object>)) {
                    breaked = true;
                    break; // Não podemos continuar se não for um Map
                }
                current = nestedMap;
            }
            if (!breaked && fieldPath.length > 0)
                current.put(fieldPath[fieldPath.length - 1], fieldError.getDefaultMessage());
        }
        return errors;
    }

}
