package com.alvaro.empresas.passagens.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.util.Date;

public record PasajeDTO(
        Integer id,
        @NotBlank
        String carnet,
        @NotBlank
        String nombre,
        @Past
        Date nascimento,
        @NotNull
        Float descuento,
        @NotNull
        Integer idViaje,
        @NotNull
        Integer nSilla
) {

}
