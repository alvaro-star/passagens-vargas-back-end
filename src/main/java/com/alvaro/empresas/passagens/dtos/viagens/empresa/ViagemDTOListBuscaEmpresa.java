package com.alvaro.empresas.passagens.dtos.viagens.empresa;

import com.alvaro.empresas.passagens.dtos.precos.PrecoDTO;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaResponseDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record ViagemDTOListBuscaEmpresa(
        UUID id,
        String logo,
        BigDecimal valorArrecadadoEfectivo,
        BigDecimal valorArrecadadoWeb,
        boolean isCobrado,
        boolean isCancelado,
        ParadaResponseDTO saida,
        ParadaResponseDTO destino,
        List<PrecoDTO> precos) {
    public ViagemDTOListBuscaEmpresa(ViagemModel model, String logo, ParadaResponseDTO saida,
                                     ParadaResponseDTO destino, List<PrecoDTO> precos) {
        this(model.getId(), logo, model.getValorArrecadadoDinheiro(), model.getValorArrecadadoWeb(),
                model.isCobrado(), model.isCancelado(), saida, destino, precos);
    }

    public ViagemDTOListBuscaEmpresa(ViagemModel model) {
        this(model.getId(), "",
                model.getValorArrecadadoDinheiro(),
                model.getValorArrecadadoWeb(),
                model.isCobrado(),
                model.isCancelado(),
                new ParadaResponseDTO(model.getSaida()),
                new ParadaResponseDTO(model.getDestino()),
                new ArrayList<>());
    }
}
