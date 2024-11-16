package com.alvaro.empresas.passagens.dtos.viajes.JPQL;

import com.alvaro.empresas.passagens.enums.TipoPagamento;

import java.math.BigDecimal;
import java.util.UUID;

public record PasajeJPQLBusca(
        Integer salidaLugarId,
        Integer destinoLugarId,
        Integer nSilla,
        Boolean compradoWeb,
        UUID facturaRembolsoId,
        Boolean enEfectivo,
        TipoPagamento metodoPago,
        BigDecimal precioPagado
) {
}
