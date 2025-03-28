package com.alvaro.empresas.passagens.paradas.dtos;

import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;

import java.util.List;

public record DepartamentoOutputDTO(
        Integer id,
        String nome,
        String abreviacao,
        List<CidadeDTO> cidades
) {
    public DepartamentoOutputDTO(DepartamentoModel model) {
        this(model.getId(), model.getNome(), model.getAbreviacao(), null);
    }

    public DepartamentoOutputDTO(DepartamentoModel model, List<CidadeDTO> cidades) {
        this(model.getId(), model.getNome(), model.getAbreviacao(), cidades);
    }
}
