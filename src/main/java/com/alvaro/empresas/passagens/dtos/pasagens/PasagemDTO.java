package com.alvaro.empresas.passagens.dtos.pasagens;

import jakarta.validation.constraints.*;

import java.util.Date;

//Las sillas necessitan estar en el mismo piso
public record PasagemDTO(
        Integer id,
        @NotBlank
        @Size(max = 7, min = 7)
        String documento,
        @NotBlank
        @Size(max = 50)
        String nome,
        @Past
        @NotNull
        Date nascimento,
        @NotNull
        @Positive
        Integer numeroAssento
) {
}
