package com.alvaro.empresas.passagens.dtos.viajes;

import com.alvaro.empresas.passagens.dtos.precios.PrecioDTO;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;

import java.util.List;
import java.util.UUID;

public record ViajeDTOResponse(
        Integer id,
        UUID idTrayecto,
        ParadaDTO salida,
        ParadaDTO destino,
        List<PrecioDTO> precios
) {
    public ViajeDTOResponse(ViajeModel model, List<PrecioDTO> precios, UUID idTrayecto, ParadaDTO salida, ParadaDTO destino) {
        this(model.getId(), idTrayecto, salida, destino, precios);
    }
}
