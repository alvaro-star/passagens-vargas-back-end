package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.security.models.UsuarioModel;

import static com.alvaro.empresas.passagens.security.services.RoleService.getCargo;

public record FuncionarioResponseDTO(
        String email,
        String nome,
        String telefone,
        String cargo
) {
    public FuncionarioResponseDTO(UsuarioModel model) {
        this(model.getEmail(), model.getNome(), model.getTelefone(), getCargo(model));
    }
}
