package com.alvaro.empresas.passagens.paradas.dtos.JPQL;

import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import lombok.Getter;

@Getter
public class ViajeEmpresaDTOJPQ {
    private final ViagemModel viaje;
    private final ParadaModel salida, destino;

    public ViajeEmpresaDTOJPQ(ViagemModel viaje, ParadaModel salida, ParadaModel destino) {
        this.viaje = viaje;
        this.salida = salida;
        this.destino = destino;
    }
}
