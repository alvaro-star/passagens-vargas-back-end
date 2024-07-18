package com.alvaro.empresas.passagens.dtos.pasajes;

import jakarta.validation.constraints.*;

import java.util.Date;

//Las sillas necessitan estar en el mismo piso
public record PasajeDTO(
        Integer id,
        @NotBlank
        @Size(max = 7, min = 7)
        String carnet,
        @NotBlank
        @Size(max = 50)
        String nombre,
        @Past
        @NotNull
        Date nascimento,
        @NotNull
        @Positive
        Integer nSilla
) {

}
