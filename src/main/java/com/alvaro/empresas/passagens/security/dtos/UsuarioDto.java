package com.alvaro.empresas.passagens.security.dtos;

import java.util.List;

public record UsuarioDto(
        String login,
        String nombre,
        String telefono,
        List<String> roles
) {
}
