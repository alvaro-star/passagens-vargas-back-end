package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.security.models.UsuarioModel;

import static com.alvaro.empresas.passagens.security.services.RoleService.getCargo;

public record FuncionarioDTO(
        String login,
        String nombre,
        String telefono,
        String cargo
) {
    public FuncionarioDTO(UsuarioModel model) {
        this(model.getLogin(), model.getNombre(), model.getTelefono(), getCargo(model));
    }
}
