package com.alvaro.empresas.passagens.onibus.dtos.pisos;

import com.alvaro.empresas.passagens.onibus.models.PisoModel;

import java.util.UUID;

public record PisoDTOResponse(
        UUID id,
        Integer nLinhas,
        Integer nColunas,
        String distribuicaoFileira,
        Integer nPiso,
        String inicioContagem,
        Integer nAssentos,
        Integer primeiroAssento,
        UUID idOnibus,
        int[] posicoesBloqueadas
) {
    public PisoDTOResponse(PisoModel modelo) {
        this(
                modelo.getId(),
                modelo.getNLinhas(),
                modelo.getNColunas(),
                modelo.getDistribuicaoFileira().toString(),
                modelo.getNPiso(),
                modelo.getInicioContagem().toString(),
                modelo.getNAssentos(),
                modelo.getPrimeiroAssento(),
                modelo.getOnibusId(),
                modelo.getPosicionesBloqueadasIntegerList()
        );
    }
}