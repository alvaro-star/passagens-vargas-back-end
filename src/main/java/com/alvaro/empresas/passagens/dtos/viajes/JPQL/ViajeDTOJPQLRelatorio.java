package com.alvaro.empresas.passagens.dtos.viajes.JPQL;

import com.alvaro.empresas.passagens.models.ViagemModel;

public record ViajeDTOJPQLRelatorio(
        ViagemModel viaje,
        Integer idSalida,
        Integer idDestino
) {
}
