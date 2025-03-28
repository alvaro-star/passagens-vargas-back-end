package com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions;

import java.util.ArrayList;
import java.util.List;

import com.alvaro.empresas.passagens.configuracoes.exceptions.dtos.FieldMessage;
import com.alvaro.empresas.passagens.services.validacao.FieldMessageList;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ValidationWithErrorListExceptions extends RuntimeException {
    private List<FieldMessageList> errorsList = new ArrayList<>();
    private List<FieldMessage> errors;

    public ValidationWithErrorListExceptions(String message, List<FieldMessage> errors,
            List<FieldMessageList> errorsList) {
        super(message);
        this.errorsList = errorsList;
        this.errors = errors;
    }
}
