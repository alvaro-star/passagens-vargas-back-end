package com.alvaro.empresas.passagens.dtos.viajes.Empresa;

import com.alvaro.empresas.passagens.dtos.precios.PrecioDTO;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ViajeDTOEmpresaResponse(
        UUID codigo,
        BigDecimal valorArrecadado,
        boolean isCobrado,
        Integer idAutobus,
        List<ParadaDTOComplete> paradas,
        List<PrecioDTO> precios
) {
    public ViajeDTOEmpresaResponse(ViajeModel model, Integer idAutobus, List<ParadaDTOComplete> paradas, List<PrecioDTO> precios) {
        this(model.getCodigo(), model.getValorArrecadado(), model.isCobrado(), idAutobus, paradas, precios);
    }
}
