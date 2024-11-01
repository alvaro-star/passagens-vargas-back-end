package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.models.EmpresaModel;

import java.math.BigDecimal;
import java.util.UUID;

public record EmpresaDTOResponse(
        UUID id,
        String nombre,
        String logo,
        Boolean isBloqueado,
        Boolean isEnabled,
        BigDecimal valorViajesEfectivo,
        BigDecimal valorViajesNoWeb,
        BigDecimal valorViajesWeb
) {
    public EmpresaDTOResponse(EmpresaModel model, BigDecimal valorViajesEfectivo, BigDecimal valorViajesNoWeb, BigDecimal valorViajesWeb) {
        this(model.getId(), model.getNombre(), model.getLogo(), model.getBloqued(), model.getEnabled(), valorViajesEfectivo, valorViajesNoWeb, valorViajesWeb);
    }

    public EmpresaDTOResponse(EmpresaModel model) {
        this(model.getId(), model.getNombre(), model.getLogo(), model.getBloqued(), model.getEnabled(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
