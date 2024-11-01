package com.alvaro.empresas.passagens.dtos.viajes.JPQL;

import com.alvaro.empresas.passagens.enums.TipoPagamentoEnum;

import java.math.BigDecimal;
import java.util.UUID;

public record PasajeJPQLBusca(
        Integer salidaLugarId,
        Integer destinoLugarId,
        Integer nSilla,
        Boolean compradoWeb,
        UUID facturaRembolsoId,
        Boolean enEfectivo,
        TipoPagamentoEnum metodoPago,
        BigDecimal precioPagado
) {
}
