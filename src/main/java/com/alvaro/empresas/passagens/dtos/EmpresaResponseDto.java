package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.models.EmpresaModel;

import java.math.BigDecimal;
import java.util.UUID;

public record EmpresaResponseDto(
        UUID id,
        String nombre,
        String logo,
        Boolean isBloqueado,
        Boolean isEnabled,
        BigDecimal valorViajesEfectivo,
        BigDecimal valorViajesNoWeb,
        BigDecimal valorViajesWeb
) {
    public EmpresaResponseDto(EmpresaModel model, BigDecimal valorViajesEfectivo, BigDecimal valorViajesNoWeb, BigDecimal valorViajesWeb) {
        this(model.getId(), model.getNombre(), model.getLogo(), model.getBloqued(), model.getEnabled(), valorViajesEfectivo, valorViajesNoWeb, valorViajesWeb);
    }
}
