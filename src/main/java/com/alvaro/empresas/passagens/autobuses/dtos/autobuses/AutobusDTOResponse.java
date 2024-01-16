package com.alvaro.empresas.passagens.autobuses.dtos.autobuses;

import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.dtos.TrayectoDTO;

import java.util.List;
import java.util.UUID;

public record AutobusDTOResponse(
        Integer id,
        String placa,
        UUID idEmpresa,
        List<PisoDTOResponse> pisos,
        List<TrayectoDTO> trayectos) {

    public AutobusDTOResponse(AutobusModel model, UUID idEmpresa, List<PisoDTOResponse> pisos, List<TrayectoDTO> trayectos) {
        this(model.getId(), model.getPlaca(), idEmpresa, pisos, trayectos);
    }

    public AutobusDTOResponse(AutobusModel model, UUID idEmpresa, List<PisoDTOResponse> pisos) {
        this(model.getId(), model.getPlaca(), idEmpresa, pisos, null);
    }
}
