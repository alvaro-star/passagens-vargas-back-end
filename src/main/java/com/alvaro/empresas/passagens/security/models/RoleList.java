package com.alvaro.empresas.passagens.security.models;


import lombok.Getter;

@Getter
public enum RoleList {
    ROLE_ADMIN("admin"),
    ROLE_CLIENTE("cliente"),
    ROLE_EMPRESA_ADMIN("empresa-admin"),
    ROLE_EMPRESA_FUNCIONARIO("empresa-funcionario");

    private String role;

    RoleList(String role) {
        this.role = role;
    }
}
