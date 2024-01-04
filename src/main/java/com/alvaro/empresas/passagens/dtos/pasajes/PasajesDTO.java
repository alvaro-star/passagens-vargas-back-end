package com.alvaro.empresas.passagens.dtos.pasajes;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PasajesDTO(
        Integer id,
        @NotNull
        Integer idViaje,
        @NotNull
        Float descuento,
        //Estos Datos son de cada numero de asiento
        @Size(min = 1)
        List<PasajeDTO> pasajes
) {
}
