package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.models.EmpresaModel;
import jakarta.validation.constraints.NotBlank;

public record EmpresaDto(
        Integer id,
        @NotBlank
        String nombre,
        @NotBlank
        String logo,
        @NotBlank
        String numeroCuenta) {


    public EmpresaDto(EmpresaModel model) {
        this(model.getId(), model.getNombre(), model.getLogo(), model.getNumeroCuenta());
    }
}
