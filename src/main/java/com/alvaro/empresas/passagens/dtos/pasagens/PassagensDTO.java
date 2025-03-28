package com.alvaro.empresas.passagens.dtos.pasagens;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record PassagensDTO(
        @NotNull
        UUID idPreco,
        @Valid
        ContatoDTO contato,
        @NotNull
        Integer idLugarSaida,
        @NotNull
        Integer idLugarDestino,
        @Size(min = 1, max = 7)
        List<PassagemDTO> passagens
) {
}
