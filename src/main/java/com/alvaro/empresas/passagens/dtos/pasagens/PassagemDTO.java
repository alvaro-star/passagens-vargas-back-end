package com.alvaro.empresas.passagens.dtos.pasagens;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Date;

public record PassagemDTO(
        @NotBlank
        @Pattern(regexp = "^\\d{11}$", message = "Deve conter exatamente 11 dígitos")
        String cpf,
        @NotBlank
        @Size(max = 50)
        String nome,
        @Past
        @NotNull
        LocalDate nascimento,
        @NotNull
        @Positive
        Integer nAssento
) {
}
