package com.alvaro.empresas.passagens.dtos.viajes.JPQL;

import com.alvaro.empresas.passagens.models.ViajeModel;

public record ViajeDTOJPQLRelatorio(
        ViajeModel viaje,
        Integer idSalida,
        Integer idDestino
) {
}
