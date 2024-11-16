package com.alvaro.empresas.passagens.configurations.exceptions.InternalException;

import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.helpers.Mensaje;
import com.alvaro.empresas.passagens.services.validacao.FieldMessageList;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ValidationWithErrorListExceptions extends RuntimeException {
    private List<FieldMessageList> errorsList = new ArrayList<>();
    private List<FieldMessage> errors;
    private Mensaje mensaje;

    public ValidationWithErrorListExceptions(String message, List<FieldMessage> errors, List<FieldMessageList> errorsList) {
        super(message);
        this.errorsList = errorsList;
        this.errors = errors;
        mensaje = new Mensaje(message);
    }
}
