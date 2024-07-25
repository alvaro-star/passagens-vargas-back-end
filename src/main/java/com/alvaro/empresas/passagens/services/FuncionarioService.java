package com.alvaro.empresas.passagens.services;

import com.alvaro.empresas.passagens.dtos.FuncionarioDTO;
import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.helpers.services.EmailService;
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

    public String determinarRole(UsuarioModel model) {
        var isAdmin = model.hasRole(RoleList.ROLE_EMPRESA_ADMIN.toString());
        if (isAdmin)
            return RoleList.ROLE_EMPRESA_ADMIN.toString();
        var isFuncionario = model.hasRole(RoleList.ROLE_EMPRESA_FUNCIONARIO.toString());
        if (isFuncionario)
            return RoleList.ROLE_EMPRESA_FUNCIONARIO.toString();
        return RoleList.ROLE_CLIENTE.toString();
    }

    public Page<FuncionarioDTO> findAllFromEmpresa(UUID idEmpresa, Pageable pageable) {
        return usuarioRepository.findByIdEmpresa(idEmpresa, pageable)
                .map(usuario -> new FuncionarioDTO(usuario.getLogin(), usuario.getNombre(), usuario.getTelefono(), determinarRole(usuario)));
    }

    @Transactional
    // El usuario debera crear-se una cuenta por si solo, el sistema solo le dara el cargo
    public Mensaje save(RegisterDtoFuncionario registerDto, UUID idEmpresa) {
        var usuario = usuarioRepository.findByEmail(registerDto.email());

        if (usuario.isEmpty())
            return new Mensaje("El usuario no esta registrado en el sistema");

        usuario.get().setIdEmpresa(idEmpresa);

        if (usuario.get().hasRole(RoleList.ROLE_EMPRESA_FUNCIONARIO.toString()))
            return new Mensaje("El funcionario ya esta relacionado con una empresa");

        boolean adicionou;
        RoleModel roleEmpresaFuncionario = roleService.getByRoleName(RoleList.ROLE_EMPRESA_FUNCIONARIO);
        adicionou = usuario.get().addRole(roleEmpresaFuncionario);
        if (!adicionou || roleEmpresaFuncionario == null)
            return new Mensaje("No se pudo elevar el cargo");
        usuarioRepository.save(usuario.get());
        return new Mensaje("");
    }

    @Transactional
    public Mensaje delete(String email) {
        var usuario = usuarioRepository.findByEmail(email);
        if (email.equals(""))
            return new Mensaje("El email no puede ser nulo");
        if (!usuario.isPresent())
            return new Mensaje("El usuario no esta registrado en el sistema");
        RoleModel roleEmpresaFuncionario = roleService.getByRoleName(RoleList.ROLE_EMPRESA_FUNCIONARIO);

        usuario.get().setIdEmpresa(null);
        if (!usuario.get().removeRole(roleEmpresaFuncionario))
            return new Mensaje("No seu pudo eliminar el cargo");

        usuarioRepository.save(usuario.get());
        return new Mensaje("");
    }
}
