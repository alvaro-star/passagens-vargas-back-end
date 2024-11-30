package com.alvaro.empresas.passagens.security.resource;

import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.security.dtos.UsuarioDTO;
import com.alvaro.empresas.passagens.security.dtos.UsuarioDTOUpdate;
import com.alvaro.empresas.passagens.security.dtos.UsuarioDTOUpdateValidation;
import com.alvaro.empresas.passagens.security.dtos.UsuarioEmpresaDTO;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.services.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@SecurityRequirement(name = "bearer-key")
public class UsuarioResource {
    @Autowired
    private UserLoguedComponent userLogued;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/mydata")
    public ResponseEntity<Object> getProfile() {
        var usuario = userLogued.getUserModel();

        boolean isEmpresa = usuario.hasRole(RoleList.ROLE_EMPRESA_FUNCIONARIO.toString()) || usuario.hasRole(RoleList.ROLE_EMPRESA_ADMIN.toString());
        if (isEmpresa)
            return ResponseEntity.ok(new UsuarioEmpresaDTO(usuario));
        return ResponseEntity.ok(new UsuarioDTO(usuario));
    }

    @PostMapping("/update")
    public ResponseEntity<Object> updateProfile(@RequestBody @Valid UsuarioDTOUpdate solicitud) {
        usuarioService.updateProfile(solicitud);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/validar_update")
    public ResponseEntity<Object> vaidateUpdate(@RequestBody @Valid UsuarioDTOUpdateValidation form) {
        usuarioService.validateUpdate(form);
        return ResponseEntity.noContent().build();
    }
}
