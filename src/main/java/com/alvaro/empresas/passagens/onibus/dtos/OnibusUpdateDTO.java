package com.alvaro.empresas.passagens.onibus.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OnibusUpdateDTO(
        @NotBlank
        @Pattern(regexp = "^\\d{4}[A-Z]{3}$", message = "Formato inválido, deve ser 1111AAA")
        String placa
) {
}
