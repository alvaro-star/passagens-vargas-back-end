package com.alvaro.empresas.passagens.services.validacao;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FieldMessageList {
    String name;
    List<FieldMessageItemList> itens;

    public void addItemError(FieldMessageItemList item) {
        this.itens.add(item);
    }
}
