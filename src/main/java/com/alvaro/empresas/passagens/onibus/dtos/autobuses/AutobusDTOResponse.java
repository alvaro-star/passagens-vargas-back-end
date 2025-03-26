package com.alvaro.empresas.passagens.onibus.dtos.autobuses;

import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoDTOResponse;
import com.alvaro.empresas.passagens.onibus.models.AutobusModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record AutobusDTOResponse(
        Integer id,
        String placa,
        Boolean enabled,
        BigDecimal valorViajesEfectivo,
        BigDecimal valorViajesNoWeb,
        BigDecimal valorViajesWeb,
        UUID idEmpresa,
        List<PisoDTOResponse> pisos
) {

    public AutobusDTOResponse(AutobusModel model, BigDecimal valorViajesEfectivo, BigDecimal valorViajesNoWeb, BigDecimal valorViajesWeb, List<PisoDTOResponse> pisos) {
        this(model.getId(), model.getPlaca(), model.isEnable(), valorViajesEfectivo, valorViajesNoWeb, valorViajesWeb, model.getEmpresaId(), pisos);
    }

    public AutobusDTOResponse(AutobusModel model, List<PisoDTOResponse> pisos) {
        this(model.getId(), model.getPlaca(), model.isEnable(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, model.getEmpresaId(), pisos);
    }

    public AutobusDTOResponse(AutobusModel model) {
        this(model.getId(), model.getPlaca(), model.isEnable(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, model.getEmpresaId(), new ArrayList<>());
    }
}
