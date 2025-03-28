package com.alvaro.empresas.passagens.dtos.viagens.JPQL;

import com.alvaro.empresas.passagens.enums.TipoPagamento;

import java.math.BigDecimal;
import java.util.UUID;

public record PassagemJPQLBusca(
        Integer saidaLugarId,
        Integer destinoLugarId,
        Integer nAssento,
        Boolean isCompradoWeb,
        UUID faturaReembolsoId,
        Boolean emDinheiro,
        TipoPagamento metodoPagamento,
        BigDecimal precoPago
) {
}
