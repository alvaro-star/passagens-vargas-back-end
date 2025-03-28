package com.alvaro.empresas.passagens.security.services;

import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.models.RoleModel;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import com.alvaro.empresas.passagens.security.repositories.RoleRepository;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public RoleModel save(RoleModel model) {
        return roleRepository.save(model);
    }

    public List<RoleModel> findAll() {
        return roleRepository.findAll();
    }

    public RoleModel getByRoleName(RoleList name) {
        var model = roleRepository.findByNome(name);
        return model.orElseThrow(() -> new EntityNotFoundException(name, RoleModel.class));
    }

    public static String getCargo(UsuarioModel model) {
        var isAdmin = model.hasRole(RoleList.ROLE_EMPRESA_ADMIN);
        if (isAdmin)
            return RoleList.ROLE_EMPRESA_ADMIN.toString();
        var isFuncionario = model.hasRole(RoleList.ROLE_EMPRESA_FUNCIONARIO);
        if (isFuncionario)
            return RoleList.ROLE_EMPRESA_FUNCIONARIO.toString();
        return RoleList.ROLE_CLIENTE.toString();
    }

}
