package com.alvaro.empresas.passagens.helpers.beans;

import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class MyUserService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioBean getUser() {
        var usuario = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = usuario.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        var usuarioModel = usuarioRepository.findByEmail(usuario.getName());
        return new UsuarioBean(usuarioModel, roles);
    }
}
