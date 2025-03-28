package com.alvaro.empresas.passagens.dtos.pasagens;

import jakarta.validation.constraints.*;

public record ContatoDTO(
        @NotBlank
        String nome,
        @Email
        String email,
        @NotNull
        @Min(value = 11111111)
        @Max(value = 99999999)
        Integer telefone
) {

}
