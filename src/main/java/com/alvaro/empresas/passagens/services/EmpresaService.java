package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.dtos.EmpresaDTO;
import com.alvaro.empresas.passagens.dtos.EmpresaDTOResponse;
import com.alvaro.empresas.passagens.helpers.validators.EmpresaEnabled;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.repositories.EmpresaRepository;
import com.alvaro.empresas.passagens.security.dtos.RegisterDtoEmpresaAdmin;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.models.RoleModel;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import com.alvaro.empresas.passagens.security.services.RoleService;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class EmpresaService {
    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private EmpresaEnabled empresaEnabled;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RoleService roleService;

    public EmpresaModel findById(UUID id) {
        Optional<EmpresaModel> model = empresaRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, EmpresaModel.class.getName()));
    }

    public EmpresaDTOResponse getOne(UUID id) {
        EmpresaModel model = this.findById(id);
        return new EmpresaDTOResponse(model);
    }

    public Page<EmpresaDTOResponse> findAll(Pageable pageable) {
        return empresaRepository.findAll(pageable).map(EmpresaDTOResponse::new);
    }

    @Transactional
    public EmpresaDTOResponse save(EmpresaDTO dto) {
        var model = new EmpresaModel();
        BeanUtils.copyProperties(dto, model, "id", "autobuses");
        model.setEnabled(true);
        model.setBloqued(false);
        var modelSaved = empresaRepository.save(model);
        return new EmpresaDTOResponse(modelSaved);
    }

    public void saveAdmin(RegisterDtoEmpresaAdmin empresaAdmin) {
        var usuario = usuarioRepository.findByEmail(empresaAdmin.email());
        if (usuario.isEmpty())
            throw new RestRuntimeException("El usuario no esta registrado en el sistema");

        var empresa = empresaRepository.existsById(empresaAdmin.idEmpresa());
        if (!empresa) throw new RestRuntimeException("La empresa no existe");
        if (usuario.get().hasRole(RoleList.ROLE_EMPRESA_ADMIN))
            throw new RestRuntimeException("El usuario ya es un administrador");

        List<RoleModel> rolesModels = roleService.findAll();
        Set<RoleModel> roles = new HashSet<>(rolesModels);

        if (usuario.get().hasRole(RoleList.ROLE_EMPRESA_FUNCIONARIO) && !usuario.get().getEmpresaId().equals(empresaAdmin.idEmpresa()))
            throw new RestRuntimeException("El usuario esta relacionado con otra empresa");

        usuario.get().setRoles(roles);
        usuario.get().setEmpresaId(empresaAdmin.idEmpresa());

        usuarioRepository.save(usuario.get());
    }

    public void removerAdmin(@NotBlank String email) {
        var usuario = usuarioRepository.findByEmail(email);

        if (usuario.isEmpty()) throw new RestRuntimeException("El usuario no esta registrado en el sistema");

        Set<RoleModel> roles = new HashSet<>();
        var roleCliente = roleService.getByRoleName(RoleList.ROLE_CLIENTE);
        roles.add(roleCliente);
        usuario.get().setRoles(roles);
        usuario.get().setEmpresaId(null);
        usuarioRepository.save(usuario.get());
    }

    public EmpresaDTOResponse update(EmpresaDTO dto, UUID id) {
        var model = this.findById(id);
        BeanUtils.copyProperties(dto, model, "id", "autobuses");
        empresaRepository.save(model);
        return new EmpresaDTOResponse(model, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public void bloquedCount(UUID id) {
        var model = this.findById(id);
        empresaEnabled.validEmpresaEnabled(id);
        model.setBloqued(!model.getBloqued());
        empresaRepository.save(model);
    }

    @Transactional
    public void delete(UUID id) {
        var model = this.findById(id);
        model.setEnabled(false);
        model.setBloqued(true);
        empresaRepository.save(model);
    }

}
