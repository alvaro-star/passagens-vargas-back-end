package com.alvaro.empresas.passagens.security.dtos.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SolicitudNewPassword(
        @Email
        @NotBlank
        String email
) {
}
