package com.alvaro.empresas.passagens.configuracoes.exceptions;

import org.springframework.validation.FieldError;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldMessage {
    private String name;
    private String message;

    public FieldMessage(FieldError error) {
        this.name = error.getField();
        this.message = error.getDefaultMessage();
    }
}
