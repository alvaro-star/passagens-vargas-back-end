package com.alvaro.empresas.passagens.dtos.pasagens;

import com.alvaro.empresas.passagens.enums.TipoPagamento;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record PasagensDTOVenta(
        @NotNull
        UUID idViaje,
        @Valid
        ContactoDTO contacto,
        @NotNull
        @Positive
        Integer idLugarSalida,
        @NotNull
        @Positive
        Integer idLugarDestino,
        @Size(min = 1)
        List<PasagemDTO> pasajes,
        @Enumerated(EnumType.STRING)
        TipoPagamento metodo
) {
}
