package com.alvaro.empresas.passagens.security.resource;

import com.alvaro.empresas.passagens.security.dtos.UsuarioDto;
import com.alvaro.empresas.passagens.security.dtos.UsuarioEmpresaDto;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/usuarios")
@SecurityRequirement(name = "bearer-key")
public class UsuarioResource {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/mydata")
    public ResponseEntity<Object> getProfile() {
        var usuarioAuth = SecurityContextHolder.getContext().getAuthentication();
        var usuario = usuarioRepository.findByEmail(usuarioAuth.getName());

        boolean isEmpresa = false;
        ArrayList<String> roles = new ArrayList<>();
        for (GrantedAuthority authority : usuarioAuth.getAuthorities()) {
            String role = authority.getAuthority();
            System.out.println(role);
            roles.add(role);
            if ("ROLE_EMPRESA_FUNCIONARIO".equals(role) || "ROLE_EMPRESA_ADMIN".equals(role))
                isEmpresa = true;
        }

        if (isEmpresa) {
            return ResponseEntity.ok(new UsuarioEmpresaDto(usuario.getLogin(), usuario.getNombre(), usuario.getTelefono(), usuario.getIdEmpresa(), roles));
        }

        return ResponseEntity.ok(new UsuarioDto(usuario.getLogin(), usuario.getNombre(), usuario.getTelefono(), roles));
    }
}
