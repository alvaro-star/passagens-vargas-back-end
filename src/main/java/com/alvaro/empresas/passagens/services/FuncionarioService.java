package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.dtos.FuncionarioDTO;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FuncionarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<FuncionarioDTO> findAllFromEmresa(UUID idEmpresa, Pageable pageable) {
        return usuarioRepository.findByIdEmpresa(idEmpresa, pageable).stream()
                .map(usuario -> new FuncionarioDTO(usuario.getLogin(), usuario.getNombre(), usuario.getTelefono()))
                .toList();
    }
}
