package com.alvaro.empresas.passagens.paradas.dtos.JPQL;

import java.util.UUID;

import com.alvaro.empresas.passagens.paradas.models.ParadaModel;

public record ViagemBuscaDTOJPQL(UUID idViagem, String logo, ParadaModel saida, ParadaModel destino) {
}