package com.alvaro.empresas.passagens.dtos.pasagens;

import java.util.List;
import java.util.UUID;

import com.alvaro.empresas.passagens.enums.TipoPagamento;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PassagensDTOVenta(
        @NotNull
        UUID idViagem,
        @Valid
        @NotNull
        ContatoInputDTO contato,
        @NotNull
        @Positive
        Integer idLugarSaida,
        @NotNull
        @Positive
        Integer idLugarDestino,
        @Size(min = 1, max = 7)
        @NotNull
        @Valid
        List<PassagemDTO> passagens,
        @NotNull
        TipoPagamento metodoPagamento
) {
}
