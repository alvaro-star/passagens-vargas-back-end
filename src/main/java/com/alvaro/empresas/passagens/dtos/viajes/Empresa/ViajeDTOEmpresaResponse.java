package com.alvaro.empresas.passagens.dtos.viajes.Empresa;

import com.alvaro.empresas.passagens.dtos.precos.PrecioDTO;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ViajeDTOEmpresaResponse(
        UUID id,
        BigDecimal valorArrecadadoEfectivo,
        BigDecimal valorArrecadadoWeb,
        boolean isCobrado,
        boolean cancelado,
        Integer idAutobus,
        List<ParadaDTOComplete> paradas,
        List<PrecioDTO> precios
) {
    public ViajeDTOEmpresaResponse(ViagemModel model, List<ParadaDTOComplete> paradas, List<PrecioDTO> precios) {
        this(model.getId(), model.getValorArrecadadoEfectivo(), model.getValorArrecadadoWeb(), model.isCobrado(), model.isCancelado(), model.getAutobusId(), paradas, precios);
    }
}
