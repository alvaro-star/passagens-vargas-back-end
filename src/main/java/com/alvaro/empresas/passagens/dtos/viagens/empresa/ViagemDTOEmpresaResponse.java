package com.alvaro.empresas.passagens.dtos.viagens.empresa;

import com.alvaro.empresas.passagens.dtos.precos.PrecoDTO;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ViagemDTOEmpresaResponse(
        UUID id,
        BigDecimal valorArrecadadoEfetivo,
        BigDecimal valorArrecadadoWeb,
        boolean isCobrado,
        boolean isCancelado,
        Integer idOnibus,
        List<ParadaDTOComplete> paradas,
        List<PrecoDTO> precos
) {
    public ViagemDTOEmpresaResponse(ViagemModel modelo, List<ParadaDTOComplete> paradas, List<PrecoDTO> precos) {
        this(modelo.getId(), modelo.getValorArrecadadoEfectivo(), modelo.getValorArrecadadoWeb(), modelo.isCobrado(), modelo.isCancelado(), modelo.getAutobusId(), paradas, precos);
    }
}
