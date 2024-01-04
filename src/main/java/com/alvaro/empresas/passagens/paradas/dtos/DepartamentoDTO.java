package com.alvaro.empresas.passagens.paradas.dtos;

import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record DepartamentoDTO(
        Integer id,
        @NotBlank(message = "Escriba un nombre valido")
        String nombre,
        List<CiudadDTO> ciudades) {

    public DepartamentoDTO(DepartamentoModel model) {
        this(model.getId(), model.getNombre(), null);
    }

    public DepartamentoDTO(DepartamentoModel model, List<CiudadDTO> ciudades) {
        this(model.getId(), model.getNombre(), ciudades);
    }
}
