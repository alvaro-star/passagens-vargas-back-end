package com.alvaro.empresas.passagens.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.dtos.FuncionarioResponseDTO;
import com.alvaro.empresas.passagens.dtos.PageOutput;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.configuracoes.validations.services.ValidEnabledEntities;
import com.alvaro.empresas.passagens.security.dtos.RegisterDTOFuncionario;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.models.RoleModel;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import com.alvaro.empresas.passagens.security.services.RoleService;

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

    public PageOutput<FuncionarioResponseDTO> findAllFromEmpresa(UUID idEmpresa, Pageable pageable) {
        userLogued.validIfIsAdminOrOwnerEmpresa(idEmpresa);
        var models = usuarioRepository.findByEmpresaId(idEmpresa, pageable);
        var dtos = models.map(FuncionarioResponseDTO::new);
        return new PageOutput<>(dtos);
    }

    @Transactional
    public void save(RegisterDTOFuncionario registerDTO, UUID idEmpresa) {
        userLogued.validIfIsMyEmpresa(idEmpresa);
        var empresa = empresaService.findById(idEmpresa);
        ValidEnabledEntities.validEmpresa(empresa);
        var usuario = usuarioRepository.findByEmail(registerDTO.email());
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
        userLogued.validIfIsMyEmpresa(idEmpresa);
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
