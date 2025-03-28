package com.alvaro.empresas.passagens.dtos.viagens.JPQL;

import com.alvaro.empresas.passagens.models.ViagemModel;

public record ViagemDTOJPQLRelatorio(
        ViagemModel viagem,
        Integer idSaida,
        Integer idDestino
) {
}
