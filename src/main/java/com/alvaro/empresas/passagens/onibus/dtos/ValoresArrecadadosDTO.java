package com.alvaro.empresas.passagens.onibus.dtos;

import java.math.BigDecimal;

public record ValoresArrecadadosDTO(
        BigDecimal valorArrecadadoDinheiro,
        BigDecimal valorArrecadadoNaoWeb,
        BigDecimal valorArrecadadoWeb
) {
}