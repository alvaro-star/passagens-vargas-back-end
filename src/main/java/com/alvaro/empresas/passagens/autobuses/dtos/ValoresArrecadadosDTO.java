package com.alvaro.empresas.passagens.autobuses.dtos;

import java.math.BigDecimal;

public record ValoresArrecadadosDTO(
        BigDecimal valorArrecadadoEfectivo,
        BigDecimal valorArrecadadoNoWeb,
        BigDecimal valorArrecadadoWeb
) {
}
