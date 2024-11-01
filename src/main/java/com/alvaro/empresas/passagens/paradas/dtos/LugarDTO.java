package com.alvaro.empresas.passagens.paradas.dtos;

import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LugarDTO(
        Integer id,
        @NotBlank
        String nombre,
        @NotNull
        Integer idCiudad) {

    public LugarDTO(LugarModel model) {
        this(model.getId(), model.getNombre(), model.getCiudadId());
    }
}
