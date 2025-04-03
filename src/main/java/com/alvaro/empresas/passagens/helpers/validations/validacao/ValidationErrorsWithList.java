package com.alvaro.empresas.passagens.helpers.validations.validacao;


import com.alvaro.empresas.passagens.configuracoes.exceptions.dtos.ValidationError;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ValidationErrorsWithList extends ValidationError {
    private List<FieldMessageList> errorsList = new ArrayList<>();

    public ValidationErrorsWithList(Long timestamp, HttpStatus status, String message, String path) {
        super(timestamp, status, message, path);
    }

    public void addErrorList(FieldMessageList erroList) {
        this.errorsList.add(erroList);
    }
}
