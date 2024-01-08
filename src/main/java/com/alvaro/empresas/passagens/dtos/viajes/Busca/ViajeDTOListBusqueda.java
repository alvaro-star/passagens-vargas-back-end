package com.alvaro.empresas.passagens.dtos.viajes.Busca;

import com.alvaro.empresas.passagens.dtos.PrecioDTO;
import com.alvaro.empresas.passagens.models.ViajeModel;

import java.util.List;

public record ViajeDTOListBusqueda(
        Integer id,
        String logo,
        ParadaDTOList salida,
        ParadaDTOList destino,
        List<PrecioDTO> precios
) {
    public ViajeDTOListBusqueda(ViajeModel model, String logo, ParadaDTOList salida, ParadaDTOList destino, List<PrecioDTO> precios) {
        this(model.getId(), logo, salida, destino, precios);
    }
}
