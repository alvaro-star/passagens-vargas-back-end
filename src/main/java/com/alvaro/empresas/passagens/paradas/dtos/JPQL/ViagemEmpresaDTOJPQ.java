package com.alvaro.empresas.passagens.paradas.dtos.JPQL;

import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;

public record ViagemEmpresaDTOJPQ(ViagemModel viagem, ParadaModel saida, ParadaModel destino) {
}
