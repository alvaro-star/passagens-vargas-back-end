package com.alvaro.empresas.passagens.autobuses.dtos.autobuses;

import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;

import java.util.List;
import java.util.UUID;

public record AutobusDTOResponse(
        Integer id,
        String placa,
        Boolean enabled,
        UUID idEmpresa,
        List<PisoDTOResponse> pisos
) {

    public AutobusDTOResponse(AutobusModel model, UUID idEmpresa, List<PisoDTOResponse> pisos) {
        this(model.getId(), model.getPlaca(), model.isEnable(), idEmpresa, pisos);
    }
}
