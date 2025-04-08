package com.alvaro.empresas.passagens.dtos.viagens.buyer;

import java.util.List;
import java.util.UUID;

import com.alvaro.empresas.passagens.dtos.precos.PrecoResponseVendaDTO;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaResponseDTO;

public record ViagemResponseVendaDTO(
                UUID id,
                UUID idOnibus,
                Boolean isCancelado,
                List<ParadaResponseDTO> paradas,
                List<PrecoResponseVendaDTO> precos) {

        public ViagemResponseVendaDTO(ViagemModel model, List<ParadaResponseDTO> paradas,
                        List<PrecoResponseVendaDTO> precos) {
                this(model.getId(), model.getOnibusId(), model.isCancelado(), paradas, precos);
        }
}