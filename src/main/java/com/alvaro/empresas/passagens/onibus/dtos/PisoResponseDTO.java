package com.alvaro.empresas.passagens.onibus.dtos;

import java.util.List;
import java.util.UUID;

import com.alvaro.empresas.passagens.helpers.utils.IntegerListStringUtil;
import com.alvaro.empresas.passagens.onibus.models.PisoModel;

public record PisoResponseDTO(
        UUID id,
        Integer nLinhas,
        Integer nColunas,
        String distribuicaoFileira,
        Integer nPiso,
        String inicioContagem,
        Integer nAssentos,
        Integer primeiroAssento,
        UUID idOnibus,
        List<Integer> posicoesBloqueadas) {
    public PisoResponseDTO(PisoModel modelo) {
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
                IntegerListStringUtil.convertStringToIntegerList(modelo.getPosicoesBloquedas()));
    }
}