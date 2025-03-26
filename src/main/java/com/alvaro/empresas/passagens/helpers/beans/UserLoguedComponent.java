package com.alvaro.empresas.passagens.helpers.beans;


import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.models.RoleModel;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserLoguedComponent {
    public UsuarioModel getUserModel() {
        var usuario = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (usuario instanceof UsuarioModel) return (UsuarioModel) usuario;
        throw new RestRuntimeException(HttpStatus.FORBIDDEN, "El usuario no inicio session");
    }

    public void validIfIsAdminOrOwnerEmpresa(UUID idEmpresa) {
        if (!isAdminOrOwnerEmpresa(idEmpresa))
            throw new RestRuntimeException(HttpStatus.FORBIDDEN, "Usted no esta relacionado a esta empresa");
    }

    public boolean isAdminOrOwnerEmpresa(UUID idEmpresa) {
        return hasRole(RoleList.ROLE_ADMIN) || isMyEmpresa(idEmpresa);
    }

    public boolean isMyEmpresa(UUID idEmpresa) {
        var user = getUserModel();
        return user.getEmpresaId() != null && user.getEmpresaId().equals(idEmpresa);
    }

    public void validIfIsMyEmpresa(UUID idEmpresa) {
        if (!isMyEmpresa(idEmpresa))
            throw new RestRuntimeException("El usuario no esta relacionado con esta empresa");
    }

    public boolean hasRole(RoleList role) {
        var user = getUserModel();
        for (RoleModel userRole : user.getRoles()) {
            if (userRole.getNombre().equals(role))
                return true;
        }
        return false;
    }
}
