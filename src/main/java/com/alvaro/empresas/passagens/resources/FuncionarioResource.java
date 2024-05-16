package com.alvaro.empresas.passagens.resources;

import com.alvaro.empresas.passagens.dtos.FuncionarioDTO;
import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.helpers.beans.MyUserService;
import com.alvaro.empresas.passagens.helpers.services.EmailService;
import com.alvaro.empresas.passagens.security.dtos.RegisterDto;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.models.RoleModel;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import com.alvaro.empresas.passagens.security.services.RoleService;
import com.alvaro.empresas.passagens.services.FuncionarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/funcionarios")
@SecurityRequirement(name = "bearer-key")
//EMPRESA_ADMIN - EMPRESA_ADMIN
public class FuncionarioResource {

    @Autowired
    private MyUserService myUserService;
    @Autowired
    private FuncionarioService funcionarioService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private EmailService emailService;

    @GetMapping("/{idEmpresa}")
    @PreAuthorize("hasRole('ROLE_ADMIN', 'ROLE_EMPRESA_ADMIN')")
    public ResponseEntity<List<FuncionarioDTO>> getAll(@PathVariable(value = "idEmpresa") UUID idEmpresa, Pageable pageable) {
        var user = myUserService.getUser();
        boolean isAdmin = false;
        for (String role : user.roles())
            isAdmin = role.equals("ROLE_ADMIN");

        if (isAdmin)
            return ResponseEntity.ok(funcionarioService.findAllFromEmresa(idEmpresa, pageable));

        if (user.idEmpresa() != null && user.idEmpresa() == idEmpresa)
            return ResponseEntity.ok(funcionarioService.findAllFromEmresa(idEmpresa, pageable));
        return ResponseEntity.badRequest().build();
    }

    @PreAuthorize("hasRole('ROLE_EMPRESA_ADMIN')")
    // Falta do desenvolvimento da lógica de cadastro de usuário
    public ResponseEntity<Object> save(@RequestBody @Valid RegisterDto registerDto) {

        String contrasena;
        if (usuarioRepository.findByLogin(registerDto.login()) != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Mensaje("Ya hay un usuario registrado"));
        var user = myUserService.getUser();
        if (user.idEmpresa() == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Mensaje("El usuário no esta relacionado a una Empresa"));

        if (usuarioRepository.findByLogin(registerDto.login()) != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Mensaje("Ya hay un usuario registrado"));

        contrasena = UUID.randomUUID().toString();
        Set<RoleModel> roles = new HashSet<>();

        String encriptedPassword = new BCryptPasswordEncoder().encode(contrasena);
        UsuarioModel newUser = new UsuarioModel(registerDto.login(), registerDto.nombre(), registerDto.telefono(), encriptedPassword);
        newUser.setIdEmpresa(user.idEmpresa());

        roles.add(roleService.getByRoleName(RoleList.ROLE_EMPRESA_FUNCIONARIO));

        newUser.setRoles(roles);
        usuarioRepository.save(newUser);
        emailService.mandarEmail("Bien benido a nuestro nuevo servicio, tu contrasenja es esta, cambiala: " + contrasena);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Mensaje("criado"));
    }
}
