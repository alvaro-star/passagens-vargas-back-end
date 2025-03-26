package com.alvaro.empresas.passagens.dtos.pasagens;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record PaagensDTO(
        @NotNull
        UUID idPrecio,
        @Valid
        ContactoDTO contacto,
        @NotNull
        @Positive
        Integer idLugarSalida,
        @NotNull
        @Positive
        Integer idLugarDestino,
        @Size(min = 1, max = 7)
        List<PasagemDTO> pasajes
) {
}
