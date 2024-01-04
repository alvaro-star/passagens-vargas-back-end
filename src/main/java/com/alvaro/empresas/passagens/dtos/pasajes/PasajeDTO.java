package com.alvaro.empresas.passagens.dtos.pasajes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.util.Date;

//Las sillas necessitan estar en el mismo piso
public record PasajeDTO(
        Integer id,
        @NotBlank
        String carnet,
        @NotBlank
        String nombre,
        @Past
        Date nascimento,
        @NotNull
        Integer nSilla
) {

}
