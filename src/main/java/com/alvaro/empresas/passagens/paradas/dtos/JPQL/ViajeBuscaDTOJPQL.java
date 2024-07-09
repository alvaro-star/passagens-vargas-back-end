package com.alvaro.empresas.passagens.paradas.dtos.JPQL;

import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ViajeBuscaDTOJPQL {
    private UUID idViaje;
    private String logo;
    private ParadaModel salida, destino;

    public ViajeBuscaDTOJPQL(UUID idViaje, String logo, ParadaModel salida, ParadaModel destino) {
        this.idViaje = idViaje;
        this.logo = logo;
        this.salida = salida;
        this.destino = destino;
    }
}
