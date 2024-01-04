package com.alvaro.empresas.passagens.autobuses.dtos;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;

public record AutobusDTOList(
        Integer id,

        String placa,

        Integer idEmpresa
) {

    public AutobusDTOList(AutobusModel model, Integer idEmpresa) {
        this(model.getId(), model.getPlaca(), idEmpresa);
    }
}
