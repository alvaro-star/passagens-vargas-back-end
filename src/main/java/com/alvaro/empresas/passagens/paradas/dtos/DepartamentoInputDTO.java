package com.alvaro.empresas.passagens.paradas.dtos;

import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record DepartamentoInputDTO(
        @NotBlank
        String nome,
        @NotBlank
        String abreviacao
) {
}