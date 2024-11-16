package com.alvaro.empresas.passagens.services.validacao;

import com.alvaro.empresas.passagens.configurations.exceptions.ValidationError;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
public class ValidationErrorsWithList extends ValidationError {
    private List<FieldMessageList> errorsList = new ArrayList<>();

    public ValidationErrorsWithList(Long timestamp, Integer status, String error, String message, String path) {
        super(timestamp, status, error, message, path);
    }

    public void addErrorList(FieldMessageList erroList) {
        this.errorsList.add(erroList);
    }
}
