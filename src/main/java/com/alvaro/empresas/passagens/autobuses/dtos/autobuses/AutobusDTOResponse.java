package com.alvaro.empresas.passagens.autobuses.dtos.autobuses;

import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOList;

import java.util.List;
import java.util.UUID;

public record AutobusDTOResponse(
        Integer id,
        String placa,
        UUID idEmpresa,
        List<PisoDTOResponse> pisos,
        List<ViajeDTOList> viajes
) {

    public AutobusDTOResponse(AutobusModel model, UUID idEmpresa, List<PisoDTOResponse> pisos, List<ViajeDTOList> viajes) {
        this(model.getId(), model.getPlaca(), idEmpresa, pisos, viajes);
    }

    public AutobusDTOResponse(AutobusModel model, UUID idEmpresa, List<PisoDTOResponse> pisos) {
        this(model.getId(), model.getPlaca(), idEmpresa, pisos, null);
    }
}
