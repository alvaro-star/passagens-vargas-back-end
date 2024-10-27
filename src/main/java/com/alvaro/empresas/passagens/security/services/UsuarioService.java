package com.alvaro.empresas.passagens.security.services;

import com.alvaro.empresas.passagens.helpers.beans.MyUserService;
import com.alvaro.empresas.passagens.helpers.services.EmailService;
import com.alvaro.empresas.passagens.security.dtos.LoginDto;
import com.alvaro.empresas.passagens.security.dtos.LoginResponseDto;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import com.alvaro.empresas.passagens.security.repositories.CodigoRepository;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import com.alvaro.empresas.passagens.security.repositories.UsuarioSolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    @Autowired
    private RoleService roleService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioSolicitudRepository usuarioSolicitudRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CodigoRepository codigoRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private MyUserService myUserService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public LoginResponseDto login(LoginDto dto) throws Exception {
        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.login(), dto.contrasena());
        var auth = authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((UsuarioModel) auth.getPrincipal());
        var refreshToken = tokenService.generateRefreshToken((UsuarioModel) auth.getPrincipal());
        return new LoginResponseDto(token, refreshToken);
    }

    public LoginResponseDto refresh(String refreshToken) throws Exception {
        if (refreshToken == null) throw new Exception("El token es nulo");
        if (refreshToken.startsWith("Bearer"))
            refreshToken = refreshToken.replace("Bearer ", "");

        String user = tokenService.validateToken(refreshToken);
        var usuario = usuarioRepository.findByEmail(user);
        if (usuario.isEmpty()) throw new Exception("Usuario invalido");
        String accessToken = tokenService.generateToken(usuario.get());
        return new LoginResponseDto(accessToken, null);
    }

    public UsuarioModel save(UsuarioModel usuarioModel) {
        return usuarioRepository.save(usuarioModel);
    }

}
