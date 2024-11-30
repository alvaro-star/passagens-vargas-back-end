package com.alvaro.empresas.passagens.security.dtos;

import com.alvaro.empresas.passagens.security.models.UsuarioModel;

import java.util.List;

public record UsuarioDTO(
        String login,
        String nombre,
        String telefono,
        List<String> roles
) {
    public UsuarioDTO(UsuarioModel usuario) {
        this(usuario.getLogin(), usuario.getNombre(), usuario.getTelefono(), usuario.rolesToListString());
    }
}
