package com.alvaro.empresas.passagens.helpers.validations.validacao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.FieldError;

import java.util.HashMap;

@Getter
@AllArgsConstructor
public class FieldMessageItemList {
    private Integer index;
    private final HashMap<String, String> errors;

    public FieldMessageItemList() {
        this.errors = new HashMap<>();
    }

    public void addError(FieldError erro) {
        this.errors.put(erro.getField(), erro.getDefaultMessage());
    }

    public void addError(String fieldName, String message) {
        this.errors.put(fieldName, message);
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
