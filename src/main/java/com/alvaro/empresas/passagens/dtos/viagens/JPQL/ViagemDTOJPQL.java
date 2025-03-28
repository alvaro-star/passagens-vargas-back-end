package com.alvaro.empresas.passagens.dtos.viagens.JPQL;

import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;

public record ViagemDTOJPQL(
        ViagemModel viagem,
        ParadaModel saida,
        ParadaModel destino
) {
}
