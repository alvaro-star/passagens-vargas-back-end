package com.alvaro.empresas.passagens.dtos.precios;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PrecioDTOUpdate(
        @NotNull
        @DecimalMin(value = "10.00")
        BigDecimal precio
) {
}
