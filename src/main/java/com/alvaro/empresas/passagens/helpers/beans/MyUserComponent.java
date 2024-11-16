package com.alvaro.empresas.passagens.helpers.beans;

import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyUserComponent {
    @Autowired
    private UsuarioRepository usuarioRepository;


    public UsuarioBean getUser() {
        var usuario = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = usuario.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        var usuarioModel = usuarioRepository.findByEmail(usuario.getName());
        return new UsuarioBean(usuarioModel.orElseThrow(() -> new ObjectNotFoundException(0, UsuarioModel.class.getName())), roles);
    }

    public UsuarioModel getUserModel() {
        var usuario = SecurityContextHolder.getContext().getAuthentication();
        var usuarioModel = usuarioRepository.findByEmail(usuario.getName());
        return usuarioModel.orElseThrow(() -> new ObjectNotFoundException(0, UsuarioModel.class.getName()));
    }
}
