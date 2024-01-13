package com.alvaro.empresas.passagens.dtos.pasajes;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record PasajesDTO(
        Integer id,
        @NotNull
        UUID idPrecio,
        @NotNull
        Float descuento,
        //Estos Datos son de cada numero de asiento
        @Size(min = 1)
        List<PasajeDTO> pasajes
) {
}
