package com.alvaro.empresas.passagens.autobuses.dtos.pisos;

import com.alvaro.empresas.passagens.autobuses.enums.EnumPosicao;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private Integer nLinhas;
    @NotNull
    private Integer nColunas;

    @NotBlank
    @Enumerated(EnumType.STRING)
    private EnumPosicao distribuicaoFileira;
    @NotBlank
    @Enumerated(EnumType.STRING)
    private EnumPosicao inicioContagem;

    private List<Integer> posicoesIndisponiveis = new ArrayList<Integer>();

}
