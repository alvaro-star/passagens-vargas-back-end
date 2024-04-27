package com.alvaro.empresas.passagens.autobuses.dtos.autobuses;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;

import java.math.BigDecimal;
import java.util.UUID;

public record AutobusDTOList(
        Integer id,
        String placa,
        BigDecimal valorViajes,
        UUID idEmpresa
) {

    public AutobusDTOList(AutobusModel model, BigDecimal valorViajes, UUID idEmpresa) {
        this(model.getId(), model.getPlaca(), valorViajes, idEmpresa);
    }
}
