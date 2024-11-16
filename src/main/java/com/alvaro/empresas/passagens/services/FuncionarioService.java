package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.configurations.exceptions.InternalException.GeneralException;
import com.alvaro.empresas.passagens.dtos.FuncionarioDTO;
import com.alvaro.empresas.passagens.helpers.validators.EmpresaEnabled;
import com.alvaro.empresas.passagens.security.dtos.RegisterDtoFuncionario;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.models.RoleModel;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import com.alvaro.empresas.passagens.security.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class FuncionarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private EmpresaEnabled empresaEnabled;

    public Page<FuncionarioDTO> findAllFromEmpresa(UUID idEmpresa, Pageable pageable) {
        Page<UsuarioModel> models = usuarioRepository.findByIdEmpresa(idEmpresa, pageable);
        return models.map(FuncionarioDTO::new);
    }

    @Transactional
    public void save(RegisterDtoFuncionario registerDto, UUID idEmpresa) {
        empresaEnabled.validEmpresaEnabled(idEmpresa);
        var usuario = usuarioRepository.findByEmail(registerDto.email());
        if (usuario.isEmpty())
            throw new GeneralException("El usuario no esta registrado en el sistema");
        usuario.get().setIdEmpresa(idEmpresa);
        if (usuario.get().hasRole(RoleList.ROLE_EMPRESA_FUNCIONARIO.toString()))
            throw new GeneralException("El funcionario ya esta relacionado con una empresa");

        boolean adicionou;
        RoleModel roleEmpresaFuncionario = roleService.getByRoleName(RoleList.ROLE_EMPRESA_FUNCIONARIO);
        adicionou = usuario.get().addRole(roleEmpresaFuncionario);
        if (!adicionou || roleEmpresaFuncionario == null)
            throw new GeneralException("No se pudo elevar el cargo");

        usuarioRepository.save(usuario.get());
    }

    @Transactional
    public void delete(String email, UUID idEmpresa) {
        empresaEnabled.validEmpresaEnabled(idEmpresa);
        var usuario = usuarioRepository.findByEmail(email);

        if (email.isEmpty())
            throw new GeneralException("El email no puede ser nulo");
        if (usuario.isEmpty())
            throw new GeneralException("El usuario no esta registrado en el sistema");
        RoleModel roleEmpresaFuncionario = roleService.getByRoleName(RoleList.ROLE_EMPRESA_FUNCIONARIO);
        usuario.get().setIdEmpresa(null);

        if (!usuario.get().removeRole(roleEmpresaFuncionario))
            throw new GeneralException("No seu pudo eliminar el cargo");
        usuarioRepository.save(usuario.get());
    }
}
