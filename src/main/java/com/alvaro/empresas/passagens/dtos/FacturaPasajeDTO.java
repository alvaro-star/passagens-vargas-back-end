package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.models.FacturaPasajeModel;

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
    public FacturaPasajeDTO(FacturaPasajeModel model) {
        this(model.getId(), model.getValorTotal(), model.getEstaPagado(), model.getMetodoPago().toString(), model.getFechaPago(), model.getCreatedAt());
    }
}
