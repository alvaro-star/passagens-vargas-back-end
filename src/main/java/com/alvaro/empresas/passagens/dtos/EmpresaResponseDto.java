package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.models.EmpresaModel;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record EmpresaResponseDto(
        UUID id,
        String nombre,
        String logo
) {
    public EmpresaResponseDto(EmpresaModel model) {
        this(model.getId(), model.getNombre(), model.getLogo());
    }
}
