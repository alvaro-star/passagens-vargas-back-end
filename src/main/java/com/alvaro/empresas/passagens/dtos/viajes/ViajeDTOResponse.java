package com.alvaro.empresas.passagens.dtos.viajes;

import com.alvaro.empresas.passagens.dtos.precios.PrecioDTO;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;

import java.util.List;
import java.util.UUID;

public record ViajeDTOResponse(
        UUID codigo,
        Integer idAutobus,
        Boolean cancelado,
        List<ParadaDTOComplete> paradas,
        List<PrecioDTO> precios
) {
    public ViajeDTOResponse(ViajeModel model, List<ParadaDTOComplete> paradas, List<PrecioDTO> precios) {
        this(model.getCodigo(), model.getAutobusId(), model.isCancelado(), paradas, precios);
    }
}
