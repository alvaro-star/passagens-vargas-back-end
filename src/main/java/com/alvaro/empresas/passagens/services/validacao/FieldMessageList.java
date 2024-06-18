package com.alvaro.empresas.passagens.services.validacao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
