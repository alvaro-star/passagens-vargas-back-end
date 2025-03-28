package com.alvaro.empresas.passagens.paradas.dtos;

import com.alvaro.empresas.passagens.paradas.models.CidadeModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CidadeDTO(
        Integer id,
        @NotBlank
        String nome,
        @NotNull
        Integer idDepartamento
) {


    public CidadeDTO(CidadeModel model) {
        this(model.getId(), model.getNome(), model.getDepartamentoId());
    }

}
