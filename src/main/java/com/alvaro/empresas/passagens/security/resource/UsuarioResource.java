package com.alvaro.empresas.passagens.security.resource;

import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.repositories.EmpresaRepository;
import com.alvaro.empresas.passagens.security.dtos.LoginDto;
import com.alvaro.empresas.passagens.security.dtos.LoginResponseDto;
import com.alvaro.empresas.passagens.security.dtos.RegisterDto;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.models.RoleModel;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import com.alvaro.empresas.passagens.security.services.RoleService;
import com.alvaro.empresas.passagens.security.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class UsuarioResource {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private EmpresaRepository empresaRepository;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid LoginDto loginDto) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(loginDto.login(), loginDto.contrasena());
            var auth = authenticationManager.authenticate(usernamePassword);
            var token = tokenService.generateToken((UsuarioModel) auth.getPrincipal());
            return ResponseEntity.ok(new LoginResponseDto(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Mensaje("Credenciales Invalidos"));
        }
    }

    @PostMapping("/register")////Pode haver mais de um administrador
    public ResponseEntity<Object> register(@RequestBody @Valid RegisterDto registerDto) {
        if (usuarioRepository.findByLogin(registerDto.login()) != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Mensaje("Ya hay un usuario registrado"));

        Set<RoleModel> roles = new HashSet<>();

        var usuario = SecurityContextHolder.getContext().getAuthentication();
        boolean logado;

        if (usuario == null || usuario.getName().equals("anonymousUser")) {
            logado = false;
        } else {
            logado = true;
        }

        String encriptedPassword = new BCryptPasswordEncoder().encode(registerDto.contrasena());
        UsuarioModel newUser = new UsuarioModel(registerDto.login(), registerDto.nombre(), registerDto.telefono(), encriptedPassword);

        switch (registerDto.role()) {
            case ROLE_ADMIN -> {
                roles.add(roleService.getByRoleName(registerDto.role()));
            }

            case ROLE_CLIENTE -> {
                if (logado)
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new Mensaje("Alguien inicio Sesion"));
                roles.add(roleService.getByRoleName(registerDto.role()));
            }

            case ROLE_EMPRESA_ADMIN -> {
                if (!logado)
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Mensaje("Token Invalido"));
                if (registerDto.idEmpresa() == null)
                    return ResponseEntity.unprocessableEntity().body(new FieldMessage("idEmpresa", "No puede ser nulo"));

                boolean roleValido = false;
                for (GrantedAuthority authority : usuario.getAuthorities()) {
                    String role = authority.getAuthority();
                    if ("ROLE_ADMIN".equals(role)) {
                        roleValido = true;
                        break;
                    }
                }
                boolean empresaValida = empresaRepository.existsById(registerDto.idEmpresa());

                if (!empresaValida)
                    return ResponseEntity.unprocessableEntity().body(new FieldMessage("idEmpresa", "La empresa no existe"));
                if (!roleValido)
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new Mensaje("Peticion Invalida"));

                roles.add(roleService.getByRoleName(RoleList.ROLE_EMPRESA_ADMIN));
                newUser.setIdEmpresa(registerDto.idEmpresa());
            }
            case ROLE_EMPRESA_FUNCIONARIO -> {
                if (!logado)
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Mensaje("Token Invalido"));

                boolean roleValido = false;
                for (GrantedAuthority authority : usuario.getAuthorities()) {
                    String role = authority.getAuthority();
                    if ("ROLE_EMPRESA_ADMIN".equals(role)) {
                        roleValido = true;
                        break;
                    }
                }

                if (!roleValido)
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Mensaje("No estas Autorizado"));

                var administradorEmpresa = usuarioRepository.findByEmail(usuario.getName());

                roles.add(roleService.getByRoleName(RoleList.ROLE_EMPRESA_ADMIN));
                newUser.setIdEmpresa(administradorEmpresa.getIdEmpresa());
            }

            default -> {
                return ResponseEntity.unprocessableEntity().body(new FieldMessage("role", "Role invalido"));
            }
        }

        newUser.setRoles(roles);
        usuarioRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Mensaje("criado"));
    }
}
