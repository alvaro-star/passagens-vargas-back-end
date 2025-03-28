package com.alvaro.empresas.passagens.security.dtos;

import com.alvaro.empresas.passagens.security.models.UsuarioModel;

import java.util.List;

public record UsuarioDTO(
        String email,
        String nome,
        String telefone,
        List<String> roles
) {
    public UsuarioDTO(UsuarioModel usuario) {
        this(usuario.getEmail(), usuario.getNome(), usuario.getTelefone(), usuario.rolesToListString());
    }
}
