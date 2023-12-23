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

@RestController
@RequestMapping("/roles")
public class RoleResource {
    @Autowired
    private RoleService roleService;

    @GetMapping("/load")
    public ResponseEntity<Object> loadRoles() {
        var admin = new RoleModel(RoleList.ADMIN);
        roleService.save(admin);
        var user = new RoleModel(RoleList.USER);
        roleService.save(user);
        var invalido = new RoleModel(RoleList.INVALIDO);
        roleService.save(invalido);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Mensaje("Criado"));
    }
}
