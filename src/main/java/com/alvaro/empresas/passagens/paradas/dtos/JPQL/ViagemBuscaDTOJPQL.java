package com.alvaro.empresas.passagens.paradas.dtos.JPQL;

import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import lombok.Getter;

import java.util.UUID;

public record ViagemBuscaDTOJPQL(UUID idViagem, String logo, ParadaModel saida, ParadaModel destino) {
}