package com.alvaro.empresas.passagens.dtos.precos;

import com.alvaro.empresas.passagens.models.PrecoModel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record PrecoDTO(
        UUID id,
        @NotNull @DecimalMin(value = "10.00") BigDecimal preco,
        @NotNull @Positive Integer nPiso,
        @NotNull Boolean cheio,
        @NotNull @Positive Integer nAssentosDisponiveis,
        @NotNull UUID idViagem) {

    public PrecoDTO(PrecoModel model) {
        this(model.getId(), model.getPreco(), model.getNPiso(), model.getCheio(), model.getNAssentosDisponiveis(),
                model.getViagemId());
    }
}
