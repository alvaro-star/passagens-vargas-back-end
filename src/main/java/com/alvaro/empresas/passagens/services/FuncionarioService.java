package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.dtos.ResponseFuncionarioDTO;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.helpers.validators.ValidEnabledEntities;
import com.alvaro.empresas.passagens.security.dtos.RegisterDTOFuncionario;
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
    private EmpresaService empresaService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserLoguedComponent userLogued;

    public Page<ResponseFuncionarioDTO> findAllFromEmpresa(UUID idEmpresa, Pageable pageable) {
        Page<UsuarioModel> models = usuarioRepository.findByEmpresaId(idEmpresa, pageable);
        return models.map(ResponseFuncionarioDTO::new);
    }

    @Transactional
    public void save(RegisterDTOFuncionario registerDto, UUID idEmpresa) {
        var empresa = empresaService.findById(idEmpresa);
        ValidEnabledEntities.validEmpresa(empresa);
        var usuario = usuarioRepository.findByEmail(registerDto.email());
        if (usuario.isEmpty())
            throw new RestRuntimeException("O usuário não está registrado no sistema");
        usuario.get().setEmpresaId(idEmpresa);
        if (usuario.get().hasRole(RoleList.ROLE_EMPRESA_FUNCIONARIO))
            throw new RestRuntimeException("O funcionário já está relacionado com uma empresa");

        boolean adicionou;
        RoleModel roleEmpresaFuncionario = roleService.getByRoleName(RoleList.ROLE_EMPRESA_FUNCIONARIO);
        adicionou = usuario.get().addRole(roleEmpresaFuncionario);
        if (!adicionou || roleEmpresaFuncionario == null)
            throw new RestRuntimeException("Não foi possível elevar o cargo");

        usuarioRepository.save(usuario.get());
    }

    @Transactional
    public void delete(String email, UUID idEmpresa) {
        var empresa = empresaService.findById(idEmpresa);
        ValidEnabledEntities.validEmpresa(empresa);
        var usuario = usuarioRepository.findByEmail(email);

        if (userLogued.getUserModel().getEmail().equals(email))
            throw new RestRuntimeException(HttpStatus.CONFLICT, "Você não pode se auto despedir");

        if (email.isEmpty())
            throw new RestRuntimeException("O email não pode ser nulo");
        if (usuario.isEmpty())
            throw new RestRuntimeException("O usuário não está registrado no sistema");
        RoleModel roleEmpresaFuncionario = roleService.getByRoleName(RoleList.ROLE_EMPRESA_FUNCIONARIO);
        RoleModel roleEmpresaAdmin = roleService.getByRoleName(RoleList.ROLE_EMPRESA_ADMIN);
        usuario.get().setEmpresaId(null);

        if (usuario.get().hasRole(RoleList.ROLE_EMPRESA_ADMIN))
            usuario.get().removeRole(roleEmpresaAdmin);
        if (!usuario.get().removeRole(roleEmpresaFuncionario))
            throw new RestRuntimeException("Não foi possível remover o cargo");
        usuarioRepository.save(usuario.get());
    }
}
