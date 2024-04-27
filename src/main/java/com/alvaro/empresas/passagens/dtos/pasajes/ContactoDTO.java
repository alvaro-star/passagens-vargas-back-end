package com.alvaro.empresas.passagens.dtos.pasajes;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContactoDTO(
        @NotBlank
        String nombre,
        @Email
        String email,
        @NotNull
        Integer telefono
) {

}
