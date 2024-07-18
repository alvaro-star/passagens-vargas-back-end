package com.alvaro.empresas.passagens.dtos.pasajes;

import jakarta.validation.constraints.*;

public record ContactoDTO(
        @NotBlank
        String nombre,
        @Email
        String email,
        @NotNull
        @Min(value = 11111111)
        @Max(value = 99999999)
        Integer telefono
) {

}
