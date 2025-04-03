package com.alvaro.empresas.passagens.dtos.viagens.JPQL;

import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;

public record ViagemWithLogoDTOJPQL(
        ViagemModel viagem,
        String logo,
        ParadaModel saida,
        ParadaModel destino
) {
    public ViagemWithLogoDTOJPQL(ViagemModel model, String logo) {
        this(model, logo, null, null);
    }
}
