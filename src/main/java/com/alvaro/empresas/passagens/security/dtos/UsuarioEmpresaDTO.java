package com.alvaro.empresas.passagens.security.dtos;

import com.alvaro.empresas.passagens.security.models.UsuarioModel;

import java.util.List;
import java.util.UUID;

public record UsuarioEmpresaDTO(
        String email,
        String nome,
        String telefone,
        UUID idEmpresa,
        List<String> roles
) {
    public UsuarioEmpresaDTO(UsuarioModel usuario) {
        this(usuario.getEmail(), usuario.getNome(), usuario.getTelefone(), usuario.getEmpresaId(), usuario.rolesToListString());
    }
}
