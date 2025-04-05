package com.alvaro.empresas.passagens.dtos.precos;

import java.math.BigDecimal;
import java.util.UUID;

import com.alvaro.empresas.passagens.models.PrecoModel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PrecoResponseDTO(
        UUID id,
        @NotNull
        @Positive
        BigDecimal preco,
        @NotNull
        @Positive
        Integer nPiso,
        @NotNull
        Boolean cheio,
        @NotNull
        @Positive
        Integer nAssentosDisponiveis,
        @NotNull
        UUID idViagem) {

    public PrecoResponseDTO(PrecoModel model) {
        this(model.getId(), model.getPreco(), model.getNPiso(), model.getCheio(), model.getNAssentosDisponiveis(),
                model.getViagemId());
    }
}
