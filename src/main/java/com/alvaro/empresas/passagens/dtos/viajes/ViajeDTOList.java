package com.alvaro.empresas.passagens.dtos.viajes;

import com.alvaro.empresas.passagens.models.ViajeModel;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ViajeDTOList(
        UUID codigo,
        BigDecimal valorArrecadadoEfectivo,
        BigDecimal valorArrecadadoWeb,
        Boolean isCobrado,
        @NotNull
        Integer idAutobus
) {
    public ViajeDTOList(ViajeModel model, Integer idAutobus) {
        this(model.getCodigo(), model.getValorArrecadadoEfectivo(),model.getValorArrecadadoWeb(), model.isCobrado(), idAutobus);
    }
}
