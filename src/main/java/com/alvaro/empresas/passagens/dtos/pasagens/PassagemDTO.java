package com.alvaro.empresas.passagens.dtos.pasagens;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

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
