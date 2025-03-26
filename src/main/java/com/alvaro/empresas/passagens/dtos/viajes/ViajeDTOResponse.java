package com.alvaro.empresas.passagens.dtos.viajes;

import com.alvaro.empresas.passagens.dtos.precos.PrecioDTO;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;

import java.util.List;
import java.util.UUID;

public record ViajeDTOResponse(
        UUID codigo,
        Integer idAutobus,
        Boolean cancelado,
        List<ParadaDTOComplete> paradas,
        List<PrecioDTO> precios) {
    public ViajeDTOResponse(ViagemModel model, List<ParadaDTOComplete> paradas, List<PrecioDTO> precios) {
        this(model.getId(), model.getAutobusId(), model.isCancelado(), paradas, precios);
    }
}
