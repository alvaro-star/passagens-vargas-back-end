package com.alvaro.empresas.passagens.configurations.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.FieldError;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FieldMessage {
    private String name;
    private String message;

    public FieldMessage(FieldError error) {
        this.message = error.getField();
        this.message = error.getDefaultMessage();
    }
}
