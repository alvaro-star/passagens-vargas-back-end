package com.alvaro.empresas.passagens.dtos.pasajes;

import com.alvaro.empresas.passagens.enums.MetodoPagamentoEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record PasajesDTOVenta(
        @NotNull
        UUID idViaje,
        @NotNull
        Float descuento,
        @Valid
        ContactoDTO contacto,
        @NotNull
        Integer idLugarSalida,
        @NotNull
        Integer idLugarDestino,
        @Size(min = 1)
        List<PasajeDTO> pasajes,
        @Enumerated(EnumType.STRING)
        MetodoPagamentoEnum metodo
) {
}
