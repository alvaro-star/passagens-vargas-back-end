package com.alvaro.empresas.passagens.autobuses.dtos;

import jakarta.validation.constraints.Max;
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
public class PisoDTO {

    @NotNull
    private Integer nLinhas;
    @NotNull
    @Max(value = 4)
    private Integer nColunas;//Tipo Onibus
    @NotBlank
    private String distribuicaoFileira;

    private String inicioContagem = "";

    @NotNull
    private Integer idAutobus;

    private List<PosicionIndisponibleDTO> posicoesIndisponiveis = new ArrayList<PosicionIndisponibleDTO>();

}
