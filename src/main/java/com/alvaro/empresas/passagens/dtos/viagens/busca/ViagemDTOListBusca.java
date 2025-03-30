package com.alvaro.empresas.passagens.dtos.viagens.busca;

import java.util.List;
import java.util.UUID;

import com.alvaro.empresas.passagens.dtos.precos.PrecoDTO;
import com.alvaro.empresas.passagens.paradas.dtos.JPQL.ViagemBuscaDTOJPQL;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaResponseDTO;

public record ViagemDTOListBusca(
        UUID id,
        String logo,
        ParadaResponseDTO saida,
        ParadaResponseDTO destino,
        List<PrecoDTO> precos
) {
    public ViagemDTOListBusca(ViagemBuscaDTOJPQL model, ParadaResponseDTO saida, ParadaResponseDTO destino, List<PrecoDTO> precos) {
        this(model.idViagem(), model.logo(), saida, destino, precos);
    }
}
