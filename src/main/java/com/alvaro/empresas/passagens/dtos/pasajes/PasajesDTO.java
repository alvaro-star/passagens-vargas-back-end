package com.alvaro.empresas.passagens.dtos.pasajes;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record PasajesDTO(
        @NotNull
        UUID idPrecio,
        @NotNull
        Float descuento,
        @Valid
        ContactoDTO contacto,
        @NotNull
        Integer idLugarSalida,
        @NotNull
        Integer idLugarDestino,
        @Size(min = 1)
        List<PasajeDTO> pasajes
) {
}
