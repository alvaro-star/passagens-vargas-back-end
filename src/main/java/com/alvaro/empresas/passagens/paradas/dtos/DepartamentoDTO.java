package com.alvaro.empresas.passagens.paradas.dtos;

import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record DepartamentoDTO(
        Integer id,
        @NotBlank(message = "Escreva um nome válido")
        String nome,
        @NotBlank(message = "Escreva uma abreviação válida")
        String abreviacao,
        List<CidadeDTO> cidades) {

    public DepartamentoDTO(DepartamentoModel model) {
        this(model.getId(), model.getNome(), model.getAbreviacao(), null);
    }

    public DepartamentoDTO(DepartamentoModel model, List<CidadeDTO> cidades) {
        this(model.getId(), model.getNome(), model.getAbreviacao(), cidades);
    }
}