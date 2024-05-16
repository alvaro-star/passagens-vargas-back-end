package com.alvaro.empresas.passagens.security.dtos;

import com.alvaro.empresas.passagens.security.models.RoleList;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record RegisterDto(
        @Email
        @NotBlank
        String login,
        @NotBlank
        String telefono,
        @NotBlank
        String contrasena,
        @NotBlank
        String nombre,
        @Enumerated(EnumType.STRING)
        RoleList role,
        UUID idEmpresa
) {
}
