package com.alvaro.empresas.passagens.security.resource;

import com.alvaro.empresas.passagens.security.dtos.LoginDto;
import com.alvaro.empresas.passagens.security.dtos.LoginResponseDto;
import com.alvaro.empresas.passagens.security.dtos.RegisterDto;
import com.alvaro.empresas.passagens.security.models.RoleModel;
import com.alvaro.empresas.passagens.security.models.UserModel;
import com.alvaro.empresas.passagens.security.repositories.UserRepository;
import com.alvaro.empresas.passagens.security.services.AuthorizationService;
import com.alvaro.empresas.passagens.security.services.RoleService;
import com.alvaro.empresas.passagens.security.services.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid LoginDto loginDto) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(loginDto.login(), loginDto.contrasena());
        var auth = authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((UserModel) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody @Valid RegisterDto registerDto) {
        if (userRepository.findByLogin(registerDto.login()) != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nao Autorizado");

        Set<RoleModel> roles = roleService.loadRole(registerDto.role());
        String encriptedPassword = new BCryptPasswordEncoder().encode(registerDto.contrasena());
        UserModel newUser = new UserModel(registerDto.login(), registerDto.nombre(), registerDto.carnet(), encriptedPassword, roles);

        userRepository.save(newUser);
        return ResponseEntity.ok().build();
    }

}
