package com.alvaro.empresas.passagens.dtos.viagens.empresa;

import com.alvaro.empresas.passagens.dtos.precos.PrecoDTO;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record ViagemDTOListBusquedaEmpresa(
        UUID id,
        String logo,
        BigDecimal valorArrecadadoEfectivo,
        BigDecimal valorArrecadadoWeb,
        boolean isCobrado,
        boolean isCancelado,
        ParadaDTOComplete saida,
        ParadaDTOComplete destino,
        List<PrecoDTO> precos) {
    public ViagemDTOListBusquedaEmpresa(ViagemModel model, String logo, ParadaDTOComplete saida,
                                        ParadaDTOComplete destino, List<PrecoDTO> precos) {
        this(model.getId(), logo, model.getValorArrecadadoDinheiro(), model.getValorArrecadadoWeb(),
                model.isCobrado(), model.isCancelado(), saida, destino, precos);
    }

    public ViagemDTOListBusquedaEmpresa(ViagemModel model) {
        this(model.getId(), "",
                model.getValorArrecadadoDinheiro(),
                model.getValorArrecadadoWeb(),
                model.isCobrado(),
                model.isCancelado(),
                new ParadaDTOComplete(model.getSaida()),
                new ParadaDTOComplete(model.getDestino()),
                new ArrayList<>());
    }
}
