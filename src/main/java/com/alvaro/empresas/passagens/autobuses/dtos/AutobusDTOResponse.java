package com.alvaro.empresas.passagens.autobuses.dtos;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.dtos.TrayectoDTO;

import java.util.List;

public record AutobusDTOResponse(
        Integer id,
        String placa,
        Integer idEmpresa,
        List<PisoDTOResponse> pisos,
        List<TrayectoDTO> trayectos) {

    public AutobusDTOResponse(AutobusModel model, Integer idEmpresa, List<PisoDTOResponse> pisos, List<TrayectoDTO> trayectos) {
        this(model.getId(), model.getPlaca(), idEmpresa, pisos, trayectos);
    }

    public AutobusDTOResponse(AutobusModel model, Integer idEmpresa, List<PisoDTOResponse> pisos) {
        this(model.getId(), model.getPlaca(), idEmpresa, pisos, null);
    }
}
