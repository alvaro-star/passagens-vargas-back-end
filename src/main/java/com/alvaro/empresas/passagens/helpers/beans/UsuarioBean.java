package com.alvaro.empresas.passagens.helpers.beans;

import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UsuarioBean {
    private String login;
    private String nombre;
    private String telefono;
    private UUID idEmpresa;
    private List<String> roles;

    public UsuarioBean(UsuarioModel usuarioModel, List<String> roles) {
        this.login = usuarioModel.getLogin();
        this.nombre = usuarioModel.getNombre();
        this.telefono = usuarioModel.getTelefono();
        this.idEmpresa = usuarioModel.getIdEmpresa();
        this.roles = roles;
    }

    public boolean isMyEmpresa(UUID idEmpresa) {
        return this.idEmpresa != null && this.idEmpresa.equals(idEmpresa);
    }

    public boolean hasRole(String role) {
        for (String roleList : this.roles)
            if (roleList.equals(role))
                return true;
        return false;
    }
}
