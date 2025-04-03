package com.alvaro.empresas.passagens.dtos.pasagens;

import com.alvaro.empresas.passagens.enums.TipoPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record PassagensDTOVenta(
        @NotNull
        UUID idViagem,
        @Valid
        ContatoInputDTO contato,
        @NotNull
        @Positive
        Integer idLugarSaida,
        @NotNull
        @Positive
        Integer idLugarDestino,
        @Size(min = 1)
        List<PassagemDTO> passagens,
        @NotNull
        TipoPagamento metodoPagamento
) {
}
