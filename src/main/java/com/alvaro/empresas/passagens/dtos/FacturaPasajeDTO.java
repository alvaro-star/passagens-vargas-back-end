package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.pagamentos.models.FaturaPasagemModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record FacturaPasajeDTO(
        UUID id,
        BigDecimal valorTotal,
        Boolean estaPagado,
        String metodoPagamento,
        LocalDateTime fechaPago,
        LocalDateTime createdAt
) {
    public FacturaPasajeDTO(FaturaPasagemModel model) {
        this(model.getId(), model.getValorTotal(), model.getEstaPago(), model.getEstaPago().toString(), model.getDataPagamento(), model.getCreatedAt());
    }
}
