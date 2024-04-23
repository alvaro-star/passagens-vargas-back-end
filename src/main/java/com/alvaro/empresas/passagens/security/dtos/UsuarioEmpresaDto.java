package com.alvaro.empresas.passagens.security.dtos;

import java.util.List;
import java.util.UUID;

public record UsuarioEmpresaDto(
        String login,
        String nombre,
        String telefono,
        UUID idEmpresa,
        List<String> roles
) {
}
