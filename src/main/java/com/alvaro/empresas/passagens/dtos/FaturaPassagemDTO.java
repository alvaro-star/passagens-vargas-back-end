package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.pagamentos.models.FaturaPassagemModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FaturaPassagemDTO(
        UUID id,
        BigDecimal valorTotal,
        Boolean estaPago,
        String metodoPago,
        LocalDateTime dataPagamento,
        LocalDateTime createdAt
) {
    public FaturaPassagemDTO(FaturaPassagemModel model) {
        this(model.getId(), model.getValorTotal(), model.getEstaPago(), model.getEstaPago().toString(), model.getDataPagamento(), model.getCreatedAt());
    }
}

