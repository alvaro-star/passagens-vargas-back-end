package com.alvaro.empresas.passagens.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.dtos.EmpresaInputDTO;
import com.alvaro.empresas.passagens.dtos.PageOutput;
import com.alvaro.empresas.passagens.helpers.validators.ValidEnabledEntities;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.repositories.EmpresaRepository;
import com.alvaro.empresas.passagens.security.dtos.RegisterDTOEmpresaAdmin;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.models.RoleModel;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import com.alvaro.empresas.passagens.security.services.RoleService;

import jakarta.validation.constraints.NotBlank;

@Service
public class EmpresaService {
    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RoleService roleService;

    public EmpresaModel findById(UUID id) {
        return empresaRepository.findByIdOrThr(id);
    }

    public PageOutput<EmpresaModel> findAll(Pageable pageable) {
        var models = empresaRepository.findAll(pageable);
        return new PageOutput<>(models);
    }

    @Transactional
    public EmpresaModel save(EmpresaInputDTO dto) {
        var model = new EmpresaModel();
        BeanUtils.copyProperties(dto, model, "id", "onibus");
        model.setHabilitado(true);
        model.setBloqueado(false);
        return empresaRepository.save(model);
    }

    public void saveAdmin(RegisterDTOEmpresaAdmin empresaAdmin) {
        var usuario = usuarioRepository.findByEmail(empresaAdmin.email());
        if (usuario.isEmpty())
            throw new RestRuntimeException("O usuário não está registrado no sistema");

        var empresa = empresaRepository.existsById(empresaAdmin.idEmpresa());
        if (!empresa)
            throw new RestRuntimeException("A empresa não existe");
        if (usuario.get().hasRole(RoleList.ROLE_EMPRESA_ADMIN))
            throw new RestRuntimeException("O usuário já é um administrador");

        List<RoleModel> rolesModels = roleService.findAll();
        Set<RoleModel> roles = new HashSet<>(rolesModels);

        if (usuario.get().hasRole(RoleList.ROLE_EMPRESA_FUNCIONARIO)
                && !usuario.get().getEmpresaId().equals(empresaAdmin.idEmpresa()))
            throw new RestRuntimeException("O usuário está relacionado com outra empresa");

        usuario.get().setRoles(roles);
        usuario.get().setEmpresaId(empresaAdmin.idEmpresa());

        usuarioRepository.save(usuario.get());
    }

    public void removerAdmin(@NotBlank String email) {
        var usuario = usuarioRepository.findByEmail(email);

        if (usuario.isEmpty())
            throw new RestRuntimeException("O usuário não está registrado no sistema");

        Set<RoleModel> roles = new HashSet<>();
        var roleCliente = roleService.getByRoleName(RoleList.ROLE_CLIENTE);
        roles.add(roleCliente);
        usuario.get().setRoles(roles);
        usuario.get().setEmpresaId(null);
        usuarioRepository.save(usuario.get());
    }

    public void update(EmpresaInputDTO dto, UUID id) {
        var model = this.findById(id);
        BeanUtils.copyProperties(dto, model, "id", "onibus");
        empresaRepository.save(model);
    }

    public void bloquedCount(UUID id) {
        var model = this.findById(id);
        ValidEnabledEntities.validEmpresa(model);
        model.setBloqueado(!model.getBloqueado());
        empresaRepository.save(model);
    }

    @Transactional
    public void delete(UUID id) {
        var model = this.findById(id);
        model.setHabilitado(false);
        model.setBloqueado(true);
        empresaRepository.save(model);
    }
}
