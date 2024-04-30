package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.models.EmpresaModel;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.UUID;

public record EmpresaResponseDto(
        UUID id,
        String nombre,
        String logo,
        BigDecimal valorViajesEfectivo,
        BigDecimal valorViajesWeb
) {
    public EmpresaResponseDto(EmpresaModel model, BigDecimal valorViajesEfectivo, BigDecimal valorViajesWeb) {
        this(model.getId(), model.getNombre(), model.getLogo(), valorViajesEfectivo, valorViajesWeb);
    }
}
