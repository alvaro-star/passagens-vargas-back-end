package com.alvaro.empresas.passagens.dtos.viajes.JPQL;

import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;

public record ViajeDTOJPQL(
        ViajeModel viaje,
        ParadaModel salida,
        ParadaModel destino
) {
}
