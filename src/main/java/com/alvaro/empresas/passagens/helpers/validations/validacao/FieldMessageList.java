package com.alvaro.empresas.passagens.helpers.validations.validacao;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FieldMessageList {
    private String name;
    private List<FieldMessageItemList> itens;

    public void addItemError(FieldMessageItemList item) {
        this.itens.add(item);
    }
}
