package com.alvaro.empresas.passagens.onibus.dtos;

import com.alvaro.empresas.passagens.configuracoes.validations.CustomValidations.OnibusPiso;
import com.alvaro.empresas.passagens.onibus.enums.TipePosicao;

import java.util.ArrayList;
import java.util.List;


@OnibusPiso
public record PisoInputDTO(
        Integer nLinhas,
        Integer nColunas,
        TipePosicao distribuicaoFileira,
        TipePosicao inicioContagem,
        List<Integer> posicoesBloquedas
) {
    public PisoInputDTO {
        if (posicoesBloquedas == null) {
            posicoesBloquedas = new ArrayList<>();
        }
    }

    public Integer nAssentos() {
        int nPosicoesDisponiveis = (posicoesBloquedas != null) ? posicoesBloquedas.size() : 0;
        return nColunas * nLinhas - nPosicoesDisponiveis;
    }

    public Integer nPosicoes() {
        return nColunas * nLinhas;
    }
}