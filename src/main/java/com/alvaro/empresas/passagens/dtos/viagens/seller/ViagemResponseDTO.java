package com.alvaro.empresas.passagens.dtos.viagens.seller;

import com.alvaro.empresas.passagens.dtos.precos.PrecoResponseDTO;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaResponseDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record ViagemResponseDTO(
        UUID id,
        BigDecimal valorArrecadadoEfetivo,
        BigDecimal valorArrecadadoWeb,
        boolean isCobrado,
        boolean isCancelado,
        UUID idOnibus,
        List<ParadaResponseDTO> paradas,
        List<PrecoResponseDTO> precos
) {
    public ViagemResponseDTO(ViagemModel modelo, List<ParadaResponseDTO> paradas, List<PrecoResponseDTO> precos) {
        this(modelo.getId(), modelo.getValorArrecadadoDinheiro(), modelo.getValorArrecadadoWeb(), modelo.isCobrado(),
                modelo.isCancelado(), modelo.getOnibusId(), paradas, precos);
    }

    public ViagemResponseDTO(ViagemModel modelo) {
        this(modelo.getId(), modelo.getValorArrecadadoDinheiro(), modelo.getValorArrecadadoWeb(), modelo.isCobrado(),
                modelo.isCancelado(), modelo.getOnibusId(), new ArrayList<>(), new ArrayList<>());
    }
}
