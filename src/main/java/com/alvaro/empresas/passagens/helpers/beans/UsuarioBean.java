package com.alvaro.empresas.passagens.helpers.beans;

import com.alvaro.empresas.passagens.security.models.UsuarioModel;

import java.util.List;
import java.util.UUID;

public record UsuarioBean(
        String login,
        String nombre,
        String telefono,
        UUID idEmpresa,
        List<String> roles
) {
    public UsuarioBean(UsuarioModel usuarioModel, List<String> roles) {
        this(usuarioModel.getLogin(), usuarioModel.getNombre(), usuarioModel.getTelefono(), usuarioModel.getIdEmpresa(), roles);
    }
}
