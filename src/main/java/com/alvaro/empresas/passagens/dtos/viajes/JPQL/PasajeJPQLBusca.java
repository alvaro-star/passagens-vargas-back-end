package com.alvaro.empresas.passagens.dtos.viajes.JPQL;

import com.alvaro.empresas.passagens.enums.TipoPago;

import java.math.BigDecimal;
import java.util.UUID;

public record PasajeJPQLBusca(
        Integer salidaLugarId,
        Integer destinoLugarId,
        Integer nSilla,
        Boolean compradoWeb,
        UUID facturaRembolsoId,
        Boolean enEfectivo,
        TipoPago metodoPago,
        BigDecimal precioPagado
) {
}
