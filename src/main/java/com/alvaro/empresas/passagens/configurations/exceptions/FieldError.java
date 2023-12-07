package com.alvaro.empresas.passagens.configurations.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldError {
    private String name;
    private String message;
}
