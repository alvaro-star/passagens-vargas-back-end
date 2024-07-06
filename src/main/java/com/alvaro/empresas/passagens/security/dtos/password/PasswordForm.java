package com.alvaro.empresas.passagens.security.dtos.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record PasswordForm(
        @NotNull
        UUID codigo,
        @NotBlank
        @Email
        @Size(min = 8)
        String email,
        @NotBlank
        String password
) {
}
