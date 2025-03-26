package com.alvaro.empresas.passagens.dtos.viajes.Empresa;

import com.alvaro.empresas.passagens.dtos.precos.PrecioDTO;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record ViajeDTOListBusquedaEmpresa(
        UUID id,
        String logo,
        BigDecimal valorArrecadadoEfectivo,
        BigDecimal valorArrecadadoWeb,
        boolean isCobrado,
        boolean cancelado,
        ParadaDTOComplete salida,
        ParadaDTOComplete destino,
        List<PrecioDTO> precios) {
    public ViajeDTOListBusquedaEmpresa(ViagemModel model, String logo, ParadaDTOComplete salida,
                                       ParadaDTOComplete destino, List<PrecioDTO> precios) {
        this(model.getId(), logo, model.getValorArrecadadoEfectivo(), model.getValorArrecadadoWeb(),
                model.isCobrado(), model.isCancelado(), salida, destino, precios);
    }

    public ViajeDTOListBusquedaEmpresa(ViagemModel model) {
        this(model.getId(), "",
                model.getValorArrecadadoEfectivo(),
                model.getValorArrecadadoWeb(),
                model.isCobrado(),
                model.isCancelado(),
                new ParadaDTOComplete(model.getSalida()),
                new ParadaDTOComplete(model.getDestino()),
                new ArrayList<>());
    }
}
