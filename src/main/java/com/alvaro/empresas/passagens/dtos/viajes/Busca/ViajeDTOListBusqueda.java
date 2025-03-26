package com.alvaro.empresas.passagens.dtos.viajes.Busca;

import java.util.List;
import java.util.UUID;

import com.alvaro.empresas.passagens.dtos.precos.PrecioDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;

public record ViajeDTOListBusqueda(
        UUID id,
        String logo,
        ParadaDTOComplete salida,
        ParadaDTOComplete destino,
        List<PrecioDTO> precios
) {
}
