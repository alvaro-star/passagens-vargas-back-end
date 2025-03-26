package com.alvaro.empresas.passagens.security.services;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import com.alvaro.empresas.passagens.enums.TypeSolicitudOperation;
import com.alvaro.empresas.passagens.helpers.services.EmailService;
import com.alvaro.empresas.passagens.security.dtos.LoginDto;
import com.alvaro.empresas.passagens.security.dtos.RegisterDto;
import com.alvaro.empresas.passagens.security.dtos.TokenDTO;
import com.alvaro.empresas.passagens.security.dtos.ValidadorDTO;
import com.alvaro.empresas.passagens.security.dtos.password.PasswordForm;
import com.alvaro.empresas.passagens.security.dtos.password.SolicitudNewPassword;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.models.RoleModel;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import com.alvaro.empresas.passagens.security.models.UsuarioSolicitudModel;
import com.alvaro.empresas.passagens.security.models.temporal.CodigoVerificacao;
import com.alvaro.empresas.passagens.security.repositories.CodigoRepository;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import com.alvaro.empresas.passagens.security.repositories.UsuarioSolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuthService {
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
    private UsuarioRepository usuarioRepository;

    public TokenDTO login(LoginDto dto) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(dto.login(), dto.contrasena());
            var auth = authenticationManager.authenticate(usernamePassword);
            var token = tokenService.generateToken((UsuarioModel) auth.getPrincipal());
            var refreshToken = tokenService.generateRefreshToken((UsuarioModel) auth.getPrincipal());
            return new TokenDTO(token, refreshToken);
        } catch (Exception e) {
            throw new RestRuntimeException("Las credenciales son inválidos");
        }
    }

    public TokenDTO refresh(String refreshToken) {
        if (refreshToken == null)
            throw new RestRuntimeException("El token es nulo");
        if (refreshToken.startsWith("Bearer "))
            refreshToken = refreshToken.replace("Bearer ", "");

        String user = tokenService.validateToken(refreshToken);
        var usuario = usuarioRepository.findByEmail(user);
        if (usuario.isEmpty()) throw new RestRuntimeException("Usuario invalido");

        String accessToken = tokenService.generateToken(usuario.get());
        return new TokenDTO(accessToken, null);
    }

    public void register(RegisterDto registerDto) {
        var usuarioLogin = usuarioRepository.findByEmail(registerDto.login());
        if (usuarioLogin.isPresent())
            throw new RestRuntimeException("Ya hay un usuario registrado");

        LocalDateTime startDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(1);
        var solicitudes = usuarioSolicitudRepository.findByEmailAfterTime(registerDto.login(), startDay, TypeSolicitudOperation.CREATE);

        if (solicitudes.size() > 5)
            throw new RestRuntimeException("Ya hubo bastantes intentos con este email por hoy");

        var usuario = SecurityContextHolder.getContext().getAuthentication();
        boolean logado;

        logado = !(usuario == null || usuario.getName().equals("anonymousUser"));
        if (logado)
            throw new RestRuntimeException(HttpStatus.UNAUTHORIZED, "Hay un usuario que inicio session");

        String encriptedPassword = this.passwordEncoder.encode(registerDto.contrasena());
        UsuarioSolicitudModel newUser = new UsuarioSolicitudModel(registerDto.login(), registerDto.nombre(), registerDto.telefono(), encriptedPassword, TypeSolicitudOperation.CREATE);
        usuarioSolicitudRepository.save(newUser);
        boolean emailEnviado;
        emailEnviado = emailService.mandarEmail(newUser.getEmail(), "Codigo de Verificacion", newUser.getNombre() + ", este es tu codigo de verificacion para tu cuenta en la aplicacion: \n" + newUser.getId().toString() + "\nNo lo compartas con nadie");
        if (!emailEnviado)
            throw new RestRuntimeException(emailService.messageEmailNotSended);
    }

    public void verificarRegistro(ValidadorDTO validadorDTO) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime halfHourAgo = now.minus(Duration.ofMinutes(30));
        var solicitudes = usuarioSolicitudRepository.findByEmailAfterTime(validadorDTO.email(), halfHourAgo, TypeSolicitudOperation.CREATE);
        var usuario = usuarioRepository.findByEmail(validadorDTO.email());
        if (usuario.isPresent())
            throw new RestRuntimeException("El usuario ya esta registrado");
        if (solicitudes.isEmpty())
            throw new RestRuntimeException("No hay solicitudes recientes");
        if (!solicitudes.get(0).getId().equals(validadorDTO.codigo()))
            throw new RestRuntimeException("El codigo de verificacion es incorrecto");

        usuarioSolicitudRepository.deleteByEmailBeforeTime(validadorDTO.email(), halfHourAgo);
        var usuaroNovo = new UsuarioModel(solicitudes.get(0));
        Set<RoleModel> roles = new HashSet<>();
        var roleCliente = roleService.getByRoleName(RoleList.ROLE_CLIENTE);
        roles.add(roleCliente);
        usuaroNovo.setRoles(roles);
        usuarioRepository.save(usuaroNovo);
    }

    public void getCodigoToRestorePassword(SolicitudNewPassword solicitud) {
        var usuario = usuarioRepository.findByEmail(solicitud.email());
        if (usuario.isEmpty()) throw new RestRuntimeException("Informe un email valido");
        LocalDateTime thrityMinutesBefore = LocalDateTime.now().minusMinutes(60);
        List<CodigoVerificacao> codigos = codigoRepository.findByEmailAfterDate(solicitud.email(), thrityMinutesBefore);
        if (codigos.size() >= 5)
            throw new RestRuntimeException("Fueron intentadas muchas solicitaciones");

        CodigoVerificacao codigo = new CodigoVerificacao();
        codigo.setEmail(solicitud.email());
        codigoRepository.save(codigo);
        var emailSended = emailService.mandarEmail(solicitud.email(), "Cambio de contrasena", "Este es tu codigo de verificacion para cambiar tu contrasena: \n" + codigo.getId() + "\nNo lo compartas con nadie");
        if (!emailSended)
            throw new RestRuntimeException(emailService.messageEmailNotSended);
    }

    public void validarPassword(PasswordForm form) {
        var codigo = codigoRepository.findById(form.codigo());
        if (codigo.isEmpty()) throw new ValidationException("codigo", "El codigo de verificacion es invalido");

        LocalDateTime thyrtyMinutesBefore = LocalDateTime.now().minusMinutes(30);

        if (codigo.get().getCreatedAt().isBefore(thyrtyMinutesBefore)) {
            codigoRepository.delete(codigo.get());
            throw new ValidationException("codigo", "El codigo ha expirado");
        }

        if (!codigo.get().getEmail().equals(form.email()))
            throw new ValidationException("email", "El codigo no le pertenece a este usuario");

        String passwordEncoder = this.passwordEncoder.encode(form.password());
        var usuario = usuarioRepository.findByEmail(form.email());
        if (usuario.isEmpty()) throw new ValidationException("email", "El usuario no existe");
        usuario.get().setContrasena(passwordEncoder);

        usuarioRepository.save(usuario.get());
        codigoRepository.deleteAllBeforeTime(form.email(), LocalDateTime.now());
    }
}
