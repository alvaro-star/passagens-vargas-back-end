package com.alvaro.empresas.passagens.onibus.dtos.pisos;

import com.alvaro.empresas.passagens.onibus.models.PisoModel;

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
        int[] posicoesBloqueadas
) {
    public PisoDTOResponse(PisoModel model) {
        this(
                model.getId(),
                model.getNLinhas(),
                model.getNColunas(),
                model.getDistribuicaoFileira().toString(),
                model.getNPiso(),
                model.getInicioContagem().toString(),
                model.getNSillas(),
                model.getPrimeraSilla(),
                model.getAutobusId(),
                model.getPosicionesBloqueadasIntegerList()
        );
    }
}
