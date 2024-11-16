package com.alvaro.empresas.passagens.security.dtos;

import java.util.List;

public record UsuarioDTO(
        String login,
        String nombre,
        String telefono,
        List<String> roles
) {
}
