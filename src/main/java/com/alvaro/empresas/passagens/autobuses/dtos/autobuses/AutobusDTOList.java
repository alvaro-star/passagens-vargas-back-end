package com.alvaro.empresas.passagens.autobuses.dtos.autobuses;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;

import java.math.BigDecimal;
import java.util.UUID;

public record AutobusDTOList(
        Integer id,
        String placa,
        Boolean enabled,
        BigDecimal valorViajesEfectivo,
        BigDecimal valorViajesNoWeb,
        BigDecimal valorViajesWeb,
        UUID idEmpresa
) {

    public AutobusDTOList(AutobusModel model, BigDecimal valorViajesEfectivo, BigDecimal valorViajesNoWeb, BigDecimal valorViajesWeb, UUID idEmpresa) {
        this(model.getId(), model.getPlaca(), model.isEnable(), valorViajesEfectivo, valorViajesNoWeb, valorViajesWeb, idEmpresa);
    }
}
