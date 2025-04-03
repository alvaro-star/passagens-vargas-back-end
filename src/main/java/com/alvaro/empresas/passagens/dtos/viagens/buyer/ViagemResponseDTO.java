package com.alvaro.empresas.passagens.dtos.viagens.buyer;

import com.alvaro.empresas.passagens.dtos.precos.PrecoResponseDTO;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaResponseDTO;

import java.util.List;
import java.util.UUID;

public record ViagemResponseDTO(
        UUID id,
        UUID idOnibus,
        String logo,
        Boolean isCancelado,
        List<ParadaResponseDTO> paradas,
        List<PrecoResponseDTO> precos) {
    public ViagemResponseDTO(ViagemModel model, String logo, List<ParadaResponseDTO> paradas, List<PrecoResponseDTO> precos) {
        this(model.getId(), model.getOnibusId(), logo, model.isCancelado(), paradas, precos);
    }
}
