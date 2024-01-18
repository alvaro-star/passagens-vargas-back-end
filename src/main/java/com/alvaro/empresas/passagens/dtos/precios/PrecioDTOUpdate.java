package com.alvaro.empresas.passagens.dtos.precios;

import jakarta.validation.constraints.NotNull;

public record PrecioDTOUpdate(
        @NotNull
        Float precio
) {
}
