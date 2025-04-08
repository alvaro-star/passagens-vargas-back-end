package com.alvaro.empresas.passagens.dtos.precos;

import java.math.BigDecimal;
import java.util.UUID;

import com.alvaro.empresas.passagens.models.PrecoModel;

public record PrecoResponseDTO(
        UUID id,
        BigDecimal preco,
        Integer nPiso,
        Boolean cheio,
        Integer nAssentosDisponiveis,
        UUID idViagem) {

    public PrecoResponseDTO(PrecoModel model) {
        this(model.getId(), model.getPreco(), model.getNPiso(), model.getCheio(), model.getNAssentosDisponiveis(),
                model.getViagemId());
    }
}
