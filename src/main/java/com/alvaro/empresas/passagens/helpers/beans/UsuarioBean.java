package com.alvaro.empresas.passagens.helpers.beans;

import com.alvaro.empresas.passagens.configurations.exceptions.InternalException.GeneralException;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

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


    public void validIfIsAdminOrOwnerEmpresa(UUID idEmpresa) {
        if (!isAdminOrOwnerEmpresa(idEmpresa))
            throw new GeneralException(HttpStatus.FORBIDDEN, "No esta autorizado a realizar esta accion");
    }

    public boolean isAdminOrOwnerEmpresa(UUID idEmpresa) {
        return hasRole(RoleList.ROLE_ADMIN.toString()) || isMyEmpresa(idEmpresa);
    }

    public boolean isMyEmpresa(UUID idEmpresa) {
        return this.idEmpresa != null && this.idEmpresa.equals(idEmpresa);
    }

    public void validIfIsMyEmpresa(UUID idEmpresa) {
        if (!isMyEmpresa(idEmpresa))
            throw new GeneralException("El usuario no esta relacionado con esta empresa");
    }

    public boolean hasRole(String role) {
        for (String roleList : this.roles)
            if (roleList.equals(role))
                return true;
        return false;
    }
}
