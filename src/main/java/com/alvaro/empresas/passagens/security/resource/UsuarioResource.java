package com.alvaro.empresas.passagens.security.resource;

import com.alvaro.empresas.passagens.helpers.beans.MyUserComponent;
import com.alvaro.empresas.passagens.security.dtos.UsuarioDto;
import com.alvaro.empresas.passagens.security.dtos.UsuarioEmpresaDto;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@SecurityRequirement(name = "bearer-key")
public class UsuarioResource {
    private final UsuarioRepository usuarioRepository;
    private final MyUserComponent myUserComponent;

    @Autowired
    public UsuarioResource(UsuarioRepository usuarioRepository, MyUserComponent myUserComponent) {
        this.usuarioRepository = usuarioRepository;
        this.myUserComponent = myUserComponent;
    }

    @GetMapping("/mydata")
    public ResponseEntity<Object> getProfile() {
        var usuario = myUserComponent.getUser();
        boolean isEmpresa = usuario.hasRole(RoleList.ROLE_EMPRESA_FUNCIONARIO.toString()) || usuario.hasRole(RoleList.ROLE_EMPRESA_ADMIN.toString());
        if (isEmpresa)
            return ResponseEntity.ok(new UsuarioEmpresaDto(usuario.getLogin(), usuario.getNombre(), usuario.getTelefono(), usuario.getIdEmpresa(), usuario.getRoles()));
        return ResponseEntity.ok(new UsuarioDto(usuario.getLogin(), usuario.getNombre(), usuario.getTelefono(), usuario.getRoles()));
    }
}
