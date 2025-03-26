package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.dtos.FuncionarioDTO;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
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
import org.springframework.http.HttpStatus;
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
    @Autowired
    private UserLoguedComponent userLogued;

    public Page<FuncionarioDTO> findAllFromEmpresa(UUID idEmpresa, Pageable pageable) {
        Page<UsuarioModel> models = usuarioRepository.findByEmpresaId(idEmpresa, pageable);
        return models.map(FuncionarioDTO::new);
    }

    @Transactional
    public void save(RegisterDtoFuncionario registerDto, UUID idEmpresa) {
        empresaEnabled.validEmpresaEnabled(idEmpresa);
        var usuario = usuarioRepository.findByEmail(registerDto.email());
        if (usuario.isEmpty())
            throw new RestRuntimeException("El usuario no esta registrado en el sistema");
        usuario.get().setEmpresaId(idEmpresa);
        if (usuario.get().hasRole(RoleList.ROLE_EMPRESA_FUNCIONARIO))
            throw new RestRuntimeException("El funcionario ya esta relacionado con una empresa");

        boolean adicionou;
        RoleModel roleEmpresaFuncionario = roleService.getByRoleName(RoleList.ROLE_EMPRESA_FUNCIONARIO);
        adicionou = usuario.get().addRole(roleEmpresaFuncionario);
        if (!adicionou || roleEmpresaFuncionario == null)
            throw new RestRuntimeException("No se pudo elevar el cargo");

        usuarioRepository.save(usuario.get());
    }

    @Transactional
    public void delete(String email, UUID idEmpresa) {
        empresaEnabled.validEmpresaEnabled(idEmpresa);
        var usuario = usuarioRepository.findByEmail(email);

        if (userLogued.getUserModel().getLogin().equals(email))
            throw new RestRuntimeException(HttpStatus.CONFLICT, "Usted no puede autodespedirse");

        if (email.isEmpty())
            throw new RestRuntimeException("El email no puede ser nulo");
        if (usuario.isEmpty())
            throw new RestRuntimeException("El usuario no esta registrado en el sistema");
        RoleModel roleEmpresaFuncionario = roleService.getByRoleName(RoleList.ROLE_EMPRESA_FUNCIONARIO);
        RoleModel roleEmpresaAdmin = roleService.getByRoleName(RoleList.ROLE_EMPRESA_ADMIN);
        usuario.get().setEmpresaId(null);

        if (usuario.get().hasRole(RoleList.ROLE_EMPRESA_ADMIN))
            usuario.get().removeRole(roleEmpresaAdmin);
        if (!usuario.get().removeRole(roleEmpresaFuncionario))
            throw new RestRuntimeException("No seu pudo eliminar el cargo");
        usuarioRepository.save(usuario.get());
    }
}
