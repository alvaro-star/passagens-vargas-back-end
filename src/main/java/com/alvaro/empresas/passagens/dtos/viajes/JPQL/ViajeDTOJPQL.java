package com.alvaro.empresas.passagens.dtos.viajes.JPQL;

import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;

public record ViajeDTOJPQL(
        ViagemModel viaje,
        ParadaModel salida,
        ParadaModel destino
) {
}
