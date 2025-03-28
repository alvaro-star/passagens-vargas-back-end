package com.alvaro.empresas.passagens.dtos.precos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PrecoDTOUpdate(
        @NotNull
        @DecimalMin(value = "10.00")
        BigDecimal preco
) {
}
