package com.alvaro.empresas.passagens.paradas.dtos;

import com.alvaro.empresas.passagens.paradas.models.LugarModel;

public record LugarResponseDTO(
        Integer id,
        String nome,
        String cidade,
        String departamento,
        String departamentoAbrev
) {
    public LugarResponseDTO(LugarModel model) {
        this(model.getId(),
                model.getNome(),
                model.getCidade().getNome(),
                model.getCidade().getDepartamento().getNome(),
                model.getCidade().getDepartamento().getAbreviacao()
        );
    }
}
