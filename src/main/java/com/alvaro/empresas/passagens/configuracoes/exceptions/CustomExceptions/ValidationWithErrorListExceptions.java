package com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions;

import java.util.HashMap;
import java.util.List;

import com.alvaro.empresas.passagens.helpers.validations.validacao.FieldMessageList;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ValidationWithErrorListExceptions extends RuntimeException {
    private List<FieldMessageList> errorsList;
    private HashMap<String, String> errors;

    public ValidationWithErrorListExceptions(String message, HashMap<String, String> errors,
                                             List<FieldMessageList> errorsList) {
        super(message);
        this.errorsList = errorsList;
        this.errors = errors;
    }
}
