package com.alvaro.empresas.passagens.security.resource;

import com.alvaro.empresas.passagens.configurations.exceptions.FieldMessage;
import com.alvaro.empresas.passagens.dtos.Mensaje;
import com.alvaro.empresas.passagens.helpers.services.EmailService;
import com.alvaro.empresas.passagens.repositories.EmpresaRepository;
import com.alvaro.empresas.passagens.security.dtos.LoginDto;
import com.alvaro.empresas.passagens.security.dtos.LoginResponseDto;
import com.alvaro.empresas.passagens.security.dtos.RegisterDto;
import com.alvaro.empresas.passagens.security.dtos.ValidadorDTO;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.models.RoleModel;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import com.alvaro.empresas.passagens.security.models.UsuarioSolicitudModel;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import com.alvaro.empresas.passagens.security.repositories.UsuarioSolicitudRepository;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthResource {
    private final UsuarioRepository usuarioRepository;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioSolicitudRepository usuarioSolicitudRepository;
    private final EmailService emailService;

    @Autowired
    public AuthResource(UsuarioRepository usuarioRepository, RoleService roleService, AuthenticationManager authenticationManager, TokenService tokenService, UsuarioSolicitudRepository usuarioSolicitudRepository, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioSolicitudRepository = usuarioSolicitudRepository;
        this.emailService = emailService;
    }

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

    //Es necessario crear una rutina para el registro de pasajes, una en el que se envien email con el codigo de verificacion
    @PostMapping("/register")
    public ResponseEntity<Mensaje> register(@RequestBody @Valid RegisterDto registerDto) {
        var usuarioLogin = usuarioRepository.findByEmail(registerDto.login());
        if (usuarioLogin.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Mensaje("Ya hay un usuario registrado"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDay = now.withHour(0).withMinute(0).withSecond(0).withNano(1);
        var solicitudes = usuarioSolicitudRepository.findByEmailAfterTime(registerDto.login(), startDay);
        if (solicitudes.size() > 5)
            return ResponseEntity.badRequest().body(new Mensaje("Ya hubo bastantes intentos con este email por hoy"));

        var usuario = SecurityContextHolder.getContext().getAuthentication();
        boolean logado;

        logado = !(usuario == null || usuario.getName().equals("anonymousUser"));
        if (logado)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Mensaje("Hay un usuario que inicio session"));
        String encriptedPassword = new BCryptPasswordEncoder().encode(registerDto.contrasena());
        UUID codigoAleatorio = UUID.randomUUID();
        UsuarioSolicitudModel newUser = new UsuarioSolicitudModel(registerDto.login(), registerDto.nombre(), registerDto.telefono(), encriptedPassword);
        newUser.setCodigoVerificacion(codigoAleatorio);
        usuarioSolicitudRepository.save(newUser);
        boolean valorLogico;

        valorLogico = emailService.mandarEmail(newUser.getEmail(), "Codigo de Verificacion", newUser.getNombre()
                + ", este es tu codigo de verificacion para tu cuenta en la aplicacion: \n"
                + codigoAleatorio.toString()
                + "\nNo lo compartas con nadie");
        if (valorLogico)
            return ResponseEntity.ok(new Mensaje("Verifique el codigo de seguridad"));
        return ResponseEntity.badRequest().body(new Mensaje("Hubo un problema con el destinatario del email"));
    }

    @PostMapping("/validar")
    public ResponseEntity<Mensaje> verificar(@RequestBody ValidadorDTO validadorDTO) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime halfHourAgo = now.minus(Duration.ofMinutes(30));
        var solicitudes = usuarioSolicitudRepository.findByEmailAfterTime(validadorDTO.email(), halfHourAgo);

        if (solicitudes.isEmpty()) {
            //usuarioSolicitudRepository.deleteByEmailBeforeTime(validadorDTO.email(), halfHourAgo);
            return ResponseEntity.badRequest().body(new Mensaje("No hay solicitudes recientes"));
        }
        if (!solicitudes.get(0).getCodigoVerificacion().equals(validadorDTO.codigo()))
            return ResponseEntity.badRequest().body(new Mensaje("El codigo de verificacion es incorrecto"));

        var usuaroNovo = new UsuarioModel(solicitudes.get(0));
        Set<RoleModel> roles = new HashSet<>();
        var roleCliente = roleService.getByRoleName(RoleList.ROLE_CLIENTE);
        roles.add(roleCliente);
        usuaroNovo.setRoles(roles);
        var usuarioNovo = usuarioRepository.save(usuaroNovo);
        emailService.mandarEmail(validadorDTO.email(), "Codigo de Verificacion",
                "Bien benido ala aplcacion, " + usuarioNovo.getNombre()
                        + " agradezemos tu registro, ahora podras comprar tus pasajes y disfrutar tur viajes");
        return ResponseEntity.status(HttpStatus.CREATED).body(new Mensaje("Usuario registrado con exito"));
        //Verificar o seu codigo
    }

}
