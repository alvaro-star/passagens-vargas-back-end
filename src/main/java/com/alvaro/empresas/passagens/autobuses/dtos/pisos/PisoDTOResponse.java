package com.alvaro.empresas.passagens.autobuses.dtos.pisos;

import com.alvaro.empresas.passagens.autobuses.models.PisoModel;

import java.util.ArrayList;
import java.util.List;

public record PisoDTOResponse(
        Long id,
        Integer nLinhas,
        Integer nColunas,//Tipo Onibus
        String distribuicaoFileira,
        Integer nPiso,
        String inicioContagem,
        Integer nSillas,
        Integer primeraSilla,
        Long idAutobus,
        List<Integer> posicoesIndisponiveis
) {
    public PisoDTOResponse(PisoModel model, Long idAutobus, List<Integer> posicoesIndisponiveis) {
        this(
                model.getId(),
                model.getNLinhas(),
                model.getNColunas(),
                model.getDistribuicaoFileira().toString(),
                model.getNPiso(),
                model.getInicioContagem().toString(),
                model.getNSillas(),
                model.getPrimeraSilla(),
                idAutobus,
                posicoesIndisponiveis
        );
    }
}
