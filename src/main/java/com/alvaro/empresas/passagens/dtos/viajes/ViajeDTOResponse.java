package com.alvaro.empresas.passagens.dtos.viajes;

import com.alvaro.empresas.passagens.dtos.PrecioDTO;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;

import java.util.List;
import java.util.UUID;

public record ViajeDTOResponse(
        Integer id,
        Integer plataforma,
        UUID idTrayecto,
        ParadaDTO salida,
        ParadaDTO destino,
        List<PrecioDTO> precios
) {
    public ViajeDTOResponse(ViajeModel model, List<PrecioDTO> precios, UUID idTrayecto, ParadaDTO salida, ParadaDTO destino) {
        this(model.getId(), model.getPlataforma(), idTrayecto, salida, destino, precios);
    }
}
