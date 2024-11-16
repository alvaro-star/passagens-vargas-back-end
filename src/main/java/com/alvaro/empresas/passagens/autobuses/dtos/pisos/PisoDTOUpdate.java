package com.alvaro.empresas.passagens.autobuses.dtos.pisos;

import com.alvaro.empresas.passagens.autobuses.enums.TypePosicao;
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
public class PisoDTOUpdate {

    @NotNull
    @Min(value = 1)
    private Integer nLinhas;
    @NotNull
    @Min(value = 1)
    @Max(value = 2)
    private Integer nColunas;

    @NotBlank
    @Enumerated(EnumType.STRING)
    private TypePosicao distribuicaoFileira;
    @NotBlank
    @Enumerated(EnumType.STRING)
    private TypePosicao inicioContagem;

    private List<Integer> posicoesIndisponiveis = new ArrayList<Integer>();

    public int getNSillas() {
        return nColunas * nLinhas;
    }
}
