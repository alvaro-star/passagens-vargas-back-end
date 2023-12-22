package com.alvaro.empresas.passagens.security.models;


import lombok.Getter;

@Getter
public enum RoleList {
    ADMIN("admin"), USER("user"), INVALIDO("invalido");

    private String role;

    RoleList(String role) {
        this.role = role;
    }
}
