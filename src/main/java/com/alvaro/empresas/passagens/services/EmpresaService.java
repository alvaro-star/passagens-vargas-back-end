package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.configurations.exceptions.InternalException.BadRequestException;
import com.alvaro.empresas.passagens.dtos.EmpresaDTO;
import com.alvaro.empresas.passagens.dtos.EmpresaDTOResponse;
import com.alvaro.empresas.passagens.helpers.Mensaje;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.repositories.EmpresaRepository;
import com.alvaro.empresas.passagens.security.dtos.RegisterDtoEmpresaAdmin;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.models.RoleModel;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import com.alvaro.empresas.passagens.security.services.RoleService;
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

    public Mensaje saveAdmin(RegisterDtoEmpresaAdmin empresaAdmin) {
        var usuario = usuarioRepository.findByEmail(empresaAdmin.email());

        if (usuario.isEmpty()) return new Mensaje("El usuario no esta registrado en el sistema");
        var empresa = empresaRepository.existsById(empresaAdmin.idEmpresa());
        if (!empresa) return new Mensaje("La empresa no existe");
        if (usuario.get().hasRole(RoleList.ROLE_EMPRESA_ADMIN.toString()))
            return new Mensaje("El usuario ya es un administrador");

        List<RoleModel> rolesModels = roleService.findAll();
        Set<RoleModel> roles = new HashSet<>(rolesModels);

        if (usuario.get().hasRole(RoleList.ROLE_EMPRESA_FUNCIONARIO.toString()) && !usuario.get().getIdEmpresa().equals(empresaAdmin.idEmpresa()))
            throw new BadRequestException("El usuario esta relacionado con otra empresa");

        usuario.get().setRoles(roles);
        usuario.get().setIdEmpresa(empresaAdmin.idEmpresa());

        usuarioRepository.save(usuario.get());
        return new Mensaje("");
    }

    public Mensaje removerAdmin(String email) {
        var usuario = usuarioRepository.findByEmail(email);
        if (email.isEmpty()) return new Mensaje("El email no puede ser nulo");
        if (!usuario.isPresent()) return new Mensaje("El usuario no esta registrado en el sistema");

        Set<RoleModel> roles = new HashSet<>();
        var roleCliente = roleService.getByRoleName(RoleList.ROLE_CLIENTE);
        roles.add(roleCliente);
        usuario.get().setRoles(roles);
        usuario.get().setIdEmpresa(null);
        usuarioRepository.save(usuario.get());
        return new Mensaje("");
    }

    public EmpresaDTOResponse update(EmpresaDTO dto, UUID id) {
        var model = this.findById(id);
        BeanUtils.copyProperties(dto, model, "id", "autobuses");
        empresaRepository.save(model);
        return new EmpresaDTOResponse(model, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public Mensaje bloquedCount(UUID id) {
        var model = this.findById(id);
        if (!model.getEnabled())
            return new Mensaje("La empresa esta deshabilitada");
        model.setBloqued(!model.getBloqued());
        empresaRepository.save(model);
        return new Mensaje("");
    }

    @Transactional
    public void delete(UUID id) {
        var model = this.findById(id);
        model.setEnabled(false);
        model.setBloqued(true);
        empresaRepository.save(model);
    }

}
