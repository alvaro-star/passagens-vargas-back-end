package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.models.EmpresaModel;

import java.math.BigDecimal;
import java.util.UUID;

public record EmpresaResponseDTO(
        UUID id,
        String nome,
        String logo,
        Boolean isBloqueado,
        Boolean isEnabled,
        BigDecimal valorViagensEfeito,
        BigDecimal valorViagensNaoWeb,
        BigDecimal valorViagensWeb
) {
    public EmpresaResponseDTO(EmpresaModel model, BigDecimal valorViagensEfeito, BigDecimal valorViagensNaoWeb, BigDecimal valorViagensWeb) {
        this(model.getId(), model.getNome(), model.getLogo(), model.getBloqueado(), model.getHabilitado(), valorViagensEfeito, valorViagensNaoWeb, valorViagensWeb);
    }

    public EmpresaResponseDTO(EmpresaModel model) {
        this(model.getId(), model.getNome(), model.getLogo(), model.getBloqueado(), model.getHabilitado(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
