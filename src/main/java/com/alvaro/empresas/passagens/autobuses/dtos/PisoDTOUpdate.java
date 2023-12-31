package com.alvaro.empresas.passagens.autobuses.dtos;

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
    private String distribuicaoFileira;

    private String inicioContagem;

    private List<PosicionIndisponibleDTO> posicoesIndisponiveis = new ArrayList<PosicionIndisponibleDTO>();

}
