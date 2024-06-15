package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.autobuses.dtos.ValoresArrecadadosDTO;
import com.alvaro.empresas.passagens.dtos.EmpresaDto;
import com.alvaro.empresas.passagens.dtos.EmpresaResponseDto;
import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.helpers.services.EmailService;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class EmpresaService {
    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private EmailService emailService;


    public EmpresaModel findById(UUID id) {
        Optional<EmpresaModel> model = empresaRepository.findById(id);
        return model.orElseThrow(() -> new ObjectNotFoundException(id, EmpresaModel.class.getName()));
    }

    public EmpresaResponseDto getOne(UUID id) {
        EmpresaModel model = this.findById(id);
        ValoresArrecadadosDTO valorArrecadado = empresaRepository.getArrecadacao(id);
        return new EmpresaResponseDto(model, valorArrecadado.valorArrecadadoEfectivo(), valorArrecadado.valorArrecadadoWeb());
    }

    public Page<EmpresaResponseDto> findAll(Pageable pageable) {
        return empresaRepository.findAll(pageable).map(model -> {
            ValoresArrecadadosDTO valorArrecadado = empresaRepository.getArrecadacao(model.getId());
            return new EmpresaResponseDto(model, valorArrecadado.valorArrecadadoEfectivo(), valorArrecadado.valorArrecadadoWeb());
        });
    }

    @Transactional
    public EmpresaResponseDto save(EmpresaDto dto) {
        var model = new EmpresaModel();
        BeanUtils.copyProperties(dto, model, "id", "autobuses");
        var modelSaved = empresaRepository.save(model);
        return new EmpresaResponseDto(modelSaved, new BigDecimal("00.0"), new BigDecimal("00.0"));
    }

    public Mensaje saveAdmin(RegisterDtoEmpresaAdmin empresaAdmin) {
        var usuario = usuarioRepository.findByEmail(empresaAdmin.email());

        if (usuario.isEmpty()) return new Mensaje("El usuario no esta registrado en el sistema");
        var empresa = empresaRepository.existsById(empresaAdmin.idEmpresa());
        if (!empresa) return new Mensaje("La empresa no existe");
        if (usuario.get().hasRole(RoleList.ROLE_EMPRESA_ADMIN.toString()))
            return new Mensaje("El usuario ya es un administrador");

        boolean adicionou;
        Set<RoleModel> roles = new HashSet<>();
        var roleEmpresaAdmin = roleService.getByRoleName(RoleList.ROLE_EMPRESA_ADMIN);
        var roleEmpresaFuncionario = roleService.getByRoleName(RoleList.ROLE_EMPRESA_FUNCIONARIO);
        var roleCliente = roleService.getByRoleName(RoleList.ROLE_CLIENTE);
        roles.add(roleEmpresaAdmin);
        roles.add(roleEmpresaFuncionario);
        roles.add(roleCliente);

        if (usuario.get().hasRole(RoleList.ROLE_EMPRESA_FUNCIONARIO.toString()) && !usuario.get().getIdEmpresa().equals(empresaAdmin.idEmpresa()))
            return new Mensaje("El usuario esta relacionado con otra empresa");

        usuario.get().setRoles(roles);
        usuario.get().setIdEmpresa(empresaAdmin.idEmpresa());

        usuarioRepository.save(usuario.get());
        return new Mensaje("");
    }

    public Mensaje removerAdmin(String email) {
        var usuario = usuarioRepository.findByEmail(email);
        if (email.equals("")) return new Mensaje("El email no puede ser nulo");
        if (!usuario.isPresent()) return new Mensaje("El usuario no esta registrado en el sistema");

        Set<RoleModel> roles = new HashSet<>();
        var roleCliente = roleService.getByRoleName(RoleList.ROLE_CLIENTE);
        roles.add(roleCliente);
        usuario.get().setRoles(roles);
        usuario.get().setIdEmpresa(null);
        usuarioRepository.save(usuario.get());
        return new Mensaje("");
    }

    public EmpresaResponseDto update(EmpresaDto dto, UUID id) {
        var model = this.findById(id);
        BeanUtils.copyProperties(dto, model, "id", "autobuses");
        ValoresArrecadadosDTO valorArrecadado = empresaRepository.getArrecadacao(id);
        EmpresaModel update = empresaRepository.save(model);
        return new EmpresaResponseDto(update, valorArrecadado.valorArrecadadoEfectivo(), valorArrecadado.valorArrecadadoWeb());
    }

    @Transactional
    public void delete(UUID id) {
        var model = this.findById(id);
        empresaRepository.delete(model);
    }

}
