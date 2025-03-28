package com.alvaro.empresas.passagens.security.dtos.senha;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NovaSenhaSolicitacao(
        @Email
        @NotBlank
        String email
) {
}
