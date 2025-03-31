package com.alvaro.empresas.passagens.onibus.dtos.pisos;

import com.alvaro.empresas.passagens.onibus.enums.TipePosicao;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PisoUpdateDTO {

    @NotNull
    @Min(value = 1)
    private Integer nLinhas;
    @NotNull
    @Min(value = 1)
    @Max(value = 2)
    private Integer nColunas;

    @NotBlank
    @Enumerated(EnumType.STRING)
    private TipePosicao distribuicaoFileira;
    @NotBlank
    @Enumerated(EnumType.STRING)
    private TipePosicao inicioContagem;

    private List<Integer> posicoesIndisponiveis = new ArrayList<Integer>();

    public int getNAssentos() {
        return nColunas * nLinhas;
    }
}