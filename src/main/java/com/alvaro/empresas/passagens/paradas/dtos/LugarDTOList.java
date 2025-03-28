package com.alvaro.empresas.passagens.paradas.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LugarDTOList{
    private Integer id;
    private String nome;
    private String cidade;
    private String departamento;
}