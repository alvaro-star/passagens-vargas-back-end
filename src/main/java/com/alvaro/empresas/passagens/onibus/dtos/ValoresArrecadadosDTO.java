package com.alvaro.empresas.passagens.onibus.dtos;

import java.math.BigDecimal;

public record ValoresArrecadadosDTO(
        BigDecimal valorArrecadadoEfectivo,
        BigDecimal valorArrecadadoNoWeb,
        BigDecimal valorArrecadadoWeb
) {
}
