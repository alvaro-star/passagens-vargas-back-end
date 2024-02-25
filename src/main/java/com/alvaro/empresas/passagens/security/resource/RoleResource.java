package com.alvaro.empresas.passagens.security.resource;

import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.models.RoleModel;
import com.alvaro.empresas.passagens.security.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class RoleResource {
    @Autowired
    private RoleService roleService;

    public ResponseEntity<Object> loadRoles() {
        var admin = new RoleModel(RoleList.ROLE_ADMIN);
        roleService.save(admin);
        var user = new RoleModel(RoleList.ROLE_CLIENTE);
        roleService.save(user);
        var empresaAdmin = new RoleModel(RoleList.ROLE_EMPRESA_ADMIN);
        roleService.save(empresaAdmin);
        var empresaFuncionario = new RoleModel(RoleList.ROLE_EMPRESA_FUNCIONARIO);
        roleService.save(empresaFuncionario);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Mensaje("Criado"));
    }
}
