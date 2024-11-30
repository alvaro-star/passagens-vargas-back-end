package com.alvaro.empresas.passagens.security.dtos;

import com.alvaro.empresas.passagens.security.models.UsuarioModel;

import java.util.List;
import java.util.UUID;

public record UsuarioEmpresaDTO(
        String login,
        String nombre,
        String telefono,
        UUID idEmpresa,
        List<String> roles
) {
    public UsuarioEmpresaDTO(UsuarioModel usuario) {
        this(usuario.getLogin(), usuario.getNombre(), usuario.getTelefono(), usuario.getEmpresaId(), usuario.rolesToListString());
    }
}
