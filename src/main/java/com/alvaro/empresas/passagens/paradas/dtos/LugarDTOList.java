package com.alvaro.empresas.passagens.paradas.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class LugarDTOList{
    private Integer id;
    private String nome;
    private String ciudad;
    private String departamento;
}