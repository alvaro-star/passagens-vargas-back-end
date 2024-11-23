package com.alvaro.empresas.passagens.dtos.pasajes;

import com.alvaro.empresas.passagens.enums.TipoPago;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record PasajesDTOVenta(
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
        List<PasajeDTO> pasajes,
        @Enumerated(EnumType.STRING)
        TipoPago metodo
) {
}
