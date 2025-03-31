package com.alvaro.empresas.passagens.onibus.dtos.pisos;

import com.alvaro.empresas.passagens.onibus.enums.TipePosicao;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PisoCreateDTO(
        @NotNull
        @Min(value = 1)
        Integer nLinhas,
        @NotNull
        @Min(value = 1)
        @Max(value = 4)
        Integer nColunas,
        @NotNull
        @Enumerated(EnumType.STRING)
        TipePosicao distribuicaoFileira,
        @NotNull
        @Enumerated(EnumType.STRING)
        TipePosicao inicioContagem,
        List<Integer> posicoesBloquedas
) {
    public Integer nAssentosDisponiveis() {
        int nPosicoesDisponiveis = (posicoesBloquedas != null) ? posicoesBloquedas.size() : 0;
        return nColunas * nLinhas - nPosicoesDisponiveis;
    }
}