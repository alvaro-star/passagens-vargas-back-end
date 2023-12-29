package com.alvaro.empresas.passagens.dtos;

import jakarta.validation.constraints.NotNull;

public record PrecioDTOUpdate(
        @NotNull
        Float precio
) {
}
