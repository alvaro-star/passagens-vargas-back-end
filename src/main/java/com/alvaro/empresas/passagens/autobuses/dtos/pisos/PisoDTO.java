package com.alvaro.empresas.passagens.autobuses.dtos.pisos;

import com.alvaro.empresas.passagens.autobuses.enums.EnumPosicao;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
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

    @NotNull
    @Enumerated(EnumType.STRING)
    private EnumPosicao distribuicaoFileira;
    @NotNull
    @Enumerated(EnumType.STRING)
    private EnumPosicao inicioContagem;

    @Valid
    private List<Integer> posicionesBloqueadas = new ArrayList<>();

}
