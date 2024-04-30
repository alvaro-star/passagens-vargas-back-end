package com.alvaro.empresas.passagens.security.services;

import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.models.RoleModel;
import com.alvaro.empresas.passagens.security.repositories.RoleRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public RoleModel save(RoleModel model) {
        return roleRepository.save(model);
    }

    public RoleModel getByRoleName(RoleList name) {
        var model = roleRepository.findByNombre(name);
        return model.orElseThrow(() -> new ObjectNotFoundException(name, RoleModel.class.getName()));
    }

    public Set<RoleModel> loadRole(String role) {
        Set<RoleModel> roles = new HashSet<>();
        switch (role) {
            case "admin":
                roles.add(this.getByRoleName(RoleList.ROLE_ADMIN));
                break;
            case "empresa-admin":
                roles.add(this.getByRoleName(RoleList.ROLE_EMPRESA_ADMIN));
                break;
            case "user":
                roles.add(this.getByRoleName(RoleList.ROLE_CLIENTE));
                break;
            case "empresa-funcionario":
                roles.add(this.getByRoleName(RoleList.ROLE_EMPRESA_FUNCIONARIO));
                break;
            default:
                throw new ValidationException(new FieldMessage("role", "El role dado es invalido"));
        }
        return roles;
    }

}
