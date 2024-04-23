package com.alvaro.empresas.passagens.dtos.viajes.Busca;

import com.alvaro.empresas.passagens.dtos.precios.PrecioDTO;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;

import java.util.List;
import java.util.UUID;

public record ViajeDTOListBusqueda(
        UUID id,
        String logo,
        ParadaDTOComplete salida,
        ParadaDTOComplete destino,
        List<PrecioDTO> precios
) {
    public ViajeDTOListBusqueda(ViajeModel model, String logo, ParadaDTOComplete salida, ParadaDTOComplete destino, List<PrecioDTO> precios) {
        this(model.getCodigo(), logo, salida, destino, precios);
    }
}
