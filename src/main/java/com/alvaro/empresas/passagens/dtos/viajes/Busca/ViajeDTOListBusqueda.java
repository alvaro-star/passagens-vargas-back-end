package com.alvaro.empresas.passagens.dtos.viajes.Busca;

import com.alvaro.empresas.passagens.dtos.precios.PrecioDTO;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;

import java.util.List;

public record ViajeDTOListBusqueda(
        Integer id,
        String logo,
        ParadaDTOComplete salida,
        ParadaDTOComplete destino,
        List<PrecioDTO> precios
) {
    public ViajeDTOListBusqueda(ViajeModel model, String logo, ParadaDTOComplete salida, ParadaDTOComplete destino, List<PrecioDTO> precios) {
        this(model.getId(), logo, salida, destino, precios);
    }
}
