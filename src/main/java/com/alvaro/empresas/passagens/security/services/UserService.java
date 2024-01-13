package com.alvaro.empresas.passagens.security.services;

import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import com.alvaro.empresas.passagens.security.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UsuarioModel save(UsuarioModel usuarioModel) {
        return userRepository.save(usuarioModel);
    }
}
