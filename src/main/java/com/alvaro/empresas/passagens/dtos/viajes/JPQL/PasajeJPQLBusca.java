package com.alvaro.empresas.passagens.dtos.viajes.JPQL;

import com.alvaro.empresas.passagens.enums.MetodoPagamentoEnum;

import java.math.BigDecimal;

public record PasajeJPQLBusca(
        Integer nSilla,
        Boolean compradoWeb,
        Boolean fueRembolsado,
        Boolean enEfectivo,
        MetodoPagamentoEnum metodoPago,
        BigDecimal precioPagado
) {
}
