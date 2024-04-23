package com.alvaro.empresas.passagens.autobuses.dtos.pisos;

import com.alvaro.empresas.passagens.autobuses.models.PisoModel;

import java.util.ArrayList;
import java.util.List;

public record PisoDTOResponse(
        Integer id,
        Integer nLinhas,
        Integer nColunas,//Tipo Onibus
        String distribuicaoFileira,
        Integer nPiso,
        String inicioContagem,
        Integer nSillas,
        Integer primeraSilla,
        Integer idAutobus,
        String posicoesBloqueadas
) {
    public PisoDTOResponse(PisoModel model, Integer idAutobus) {
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
                model.getPosicionesBloqueadas()
        );
    }
}
