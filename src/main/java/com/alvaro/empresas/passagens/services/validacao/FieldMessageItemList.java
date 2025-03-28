package com.alvaro.empresas.passagens.services.validacao;

import com.alvaro.empresas.passagens.configuracoes.exceptions.dtos.FieldMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FieldMessageItemList {
    Integer index;
    List<FieldMessage> errors = new ArrayList<>();

    public void addError(FieldMessage erro) {
        this.errors.add(erro);
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
