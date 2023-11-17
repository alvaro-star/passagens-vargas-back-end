package com.alvaro.empresas.passagens.security.dtos;

import com.alvaro.empresas.passagens.security.models.UserRole;

public record RegisterDto(String login, String contrasena, String nombre, UserRole role) {
}
