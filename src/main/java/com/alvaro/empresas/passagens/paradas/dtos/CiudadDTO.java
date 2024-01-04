package com.alvaro.empresas.passagens.paradas.dtos;

import com.alvaro.empresas.passagens.paradas.models.CiudadModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CiudadDTO(
        Integer id,
        @NotBlank
        String nombre,
        @NotNull
        Integer idDepartamento
) {


    public CiudadDTO(CiudadModel model) {
        this(model.getId(), model.getNombre(), null);
    }

    public CiudadDTO(CiudadModel model, Integer idDepartamento) {
        this(model.getId(), model.getNombre(), idDepartamento);
    }
}
