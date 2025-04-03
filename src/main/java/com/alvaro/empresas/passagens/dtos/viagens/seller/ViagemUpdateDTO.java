package com.alvaro.empresas.passagens.dtos.viagens.seller;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ViagemUpdateDTO(
        @NotNull UUID idOnibus) {
}
