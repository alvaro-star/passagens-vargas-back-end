package com.alvaro.empresas.passagens.dtos.viagens;

import com.alvaro.empresas.passagens.dtos.precos.PrecoDTO;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaResponseDTO;

import java.util.List;
import java.util.UUID;

public record ViagemDTOResponse(
        UUID id,
        UUID idOnibus,
        Boolean isCancelado,
        List<ParadaResponseDTO> paradas,
        List<PrecoDTO> precos) {
    public ViagemDTOResponse(ViagemModel model, List<ParadaResponseDTO> paradas, List<PrecoDTO> precos) {
        this(model.getId(), model.getOnibusId(), model.isCancelado(), paradas, precos);
    }
}
