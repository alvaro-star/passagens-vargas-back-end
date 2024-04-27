package com.alvaro.empresas.passagens.dtos.precios;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PrecioDTOUpdate(
        @NotNull
        BigDecimal precio
) {
}
