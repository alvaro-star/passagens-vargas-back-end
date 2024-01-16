package com.alvaro.empresas.passagens.autobuses.dtos.autobuses;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;

import java.util.UUID;

public record AutobusDTOList(
        Integer id,
        String placa,
        UUID idEmpresa
) {

    public AutobusDTOList(AutobusModel model, UUID idEmpresa) {
        this(model.getId(), model.getPlaca(), idEmpresa);
    }
}
