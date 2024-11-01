package com.alvaro.empresas.passagens.security.resource;

import com.alvaro.empresas.passagens.configurations.exceptions.ValidationException;
import com.alvaro.empresas.passagens.helpers.Mensaje;
import com.alvaro.empresas.passagens.enums.EnumTypeSolicitudOperation;
import com.alvaro.empresas.passagens.helpers.beans.MyUserComponent;
import com.alvaro.empresas.passagens.helpers.services.EmailService;
import com.alvaro.empresas.passagens.security.dtos.*;
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
import com.alvaro.empresas.passagens.security.services.RoleService;
import com.alvaro.empresas.passagens.security.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthResource {
    private final UsuarioRepository usuarioRepository;
    private final RoleService roleService;
    private final UsuarioSolicitudRepository usuarioSolicitudRepository;
    private final EmailService emailService;
    private final CodigoRepository codigoRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MyUserComponent myUserComponent;
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    public AuthResource(UsuarioRepository usuarioRepository,
                        RoleService roleService,
                        UsuarioSolicitudRepository usuarioSolicitudRepository,
                        EmailService emailService,
                        CodigoRepository codigoRepository,
                        MyUserComponent myUserComponent,
                        BCryptPasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.roleService = roleService;
        this.myUserComponent = myUserComponent;
        this.usuarioSolicitudRepository = usuarioSolicitudRepository;
        this.emailService = emailService;
        this.codigoRepository = codigoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid LoginDto loginDto) {
        try {
            var loginDtoResponse = usuarioService.login(loginDto);
            return ResponseEntity.ok(loginDtoResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Mensaje("El email o la contrasenha es invalido"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@RequestBody Map<String, String> request) {
        try {
            var loginDtoResponse = usuarioService.refresh(request.get("refreshToken"));
            return ResponseEntity.ok(loginDtoResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Mensaje("El token es invalido"));
        }
    }

    //Es necessario crear una rutina para el registro de pasajes, una en el que se envien email con el codigo de verificacion
    @PostMapping("/register")
    public ResponseEntity<Mensaje> register(@RequestBody @Valid RegisterDto registerDto) {
        var usuarioLogin = usuarioRepository.findByEmail(registerDto.login());
        if (usuarioLogin.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Mensaje("Ya hay un usuario registrado"));

        LocalDateTime startDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(1);
        var solicitudes = usuarioSolicitudRepository.findByEmailAfterTime(registerDto.login(), startDay, EnumTypeSolicitudOperation.CREATE);

        if (solicitudes.size() > 5)
            return ResponseEntity.badRequest().body(new Mensaje("Ya hubo bastantes intentos con este email por hoy"));

        var usuario = SecurityContextHolder.getContext().getAuthentication();
        boolean logado;

        logado = !(usuario == null || usuario.getName().equals("anonymousUser"));
        if (logado)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Mensaje("Hay un usuario que inicio session"));
        String encriptedPassword = this.passwordEncoder.encode(registerDto.contrasena());
        UsuarioSolicitudModel newUser = new UsuarioSolicitudModel(registerDto.login(), registerDto.nombre(), registerDto.telefono(), encriptedPassword, EnumTypeSolicitudOperation.CREATE);
        usuarioSolicitudRepository.save(newUser);
        boolean valorLogico;

        valorLogico = emailService.mandarEmail(newUser.getEmail(), "Codigo de Verificacion", newUser.getNombre() + ", este es tu codigo de verificacion para tu cuenta en la aplicacion: \n" + newUser.getId().toString() + "\nNo lo compartas con nadie");
        if (valorLogico) return ResponseEntity.ok(new Mensaje("Verifique el codigo de seguridad"));
        return ResponseEntity.badRequest().body(new Mensaje("Hubo un problema con el destinatario del email"));
    }

    @PostMapping("/validar")
    public ResponseEntity<Mensaje> verificarRegistro(@RequestBody ValidadorDTO validadorDTO) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime halfHourAgo = now.minus(Duration.ofMinutes(30));
        var solicitudes = usuarioSolicitudRepository.findByEmailAfterTime(validadorDTO.email(), halfHourAgo, EnumTypeSolicitudOperation.CREATE);
        var usuario = usuarioRepository.findByEmail(validadorDTO.email());
        if (usuario.isPresent()) return ResponseEntity.badRequest().body(new Mensaje("El usuario ya esta registrado"));
        if (solicitudes.isEmpty()) {
            return ResponseEntity.badRequest().body(new Mensaje("No hay solicitudes recientes"));
        }
        if (!solicitudes.get(0).getId().equals(validadorDTO.codigo()))
            return ResponseEntity.badRequest().body(new Mensaje("El codigo de verificacion es incorrecto"));

        usuarioSolicitudRepository.deleteByEmailBeforeTime(validadorDTO.email(), halfHourAgo);
        var usuaroNovo = new UsuarioModel(solicitudes.get(0));
        Set<RoleModel> roles = new HashSet<>();
        var roleCliente = roleService.getByRoleName(RoleList.ROLE_CLIENTE);
        roles.add(roleCliente);
        usuaroNovo.setRoles(roles);
        usuarioRepository.save(usuaroNovo);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Mensaje("Usuario registrado con exito"));
        //Verificar o seu codigo
    }

    @PostMapping("/forget_password")
    public ResponseEntity<Object> getCodigoToRestorePassword(@RequestBody @Valid SolicitudNewPassword solicitud) {
        var usuario = usuarioRepository.findByEmail(solicitud.email());
        if (usuario.isEmpty()) return ResponseEntity.badRequest().body(new Mensaje("Informe un email valido"));
        LocalDateTime thrityMinutesBefore = LocalDateTime.now().minusMinutes(60);
        List<CodigoVerificacao> codigos = codigoRepository.findByEmailAfterDate(solicitud.email(), thrityMinutesBefore);
        if (codigos.size() >= 5)
            return ResponseEntity.badRequest().body(new Mensaje("Fueron intentadas muchas solicitaciones"));

        CodigoVerificacao codigo = new CodigoVerificacao();
        codigo.setEmail(solicitud.email());
        codigoRepository.save(codigo);
        emailService.mandarEmail(solicitud.email(), "Cambio de contrasena", "Este es tu codigo de verificacion para cambiar tu contrasena: \n" + codigo.getId() + "\nNo lo compartas con nadie");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reset_password")
    public ResponseEntity<Object> validarPassword(@RequestBody @Valid PasswordForm form) {
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
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/update")
    public ResponseEntity<Object> updateProfile(@RequestBody @Valid UsuarioDTOUpdate solicitud) {
        var usuarioModel = myUserComponent.getUserModel();
        Boolean emailOcuped = false;
        if (solicitud.email() != null && !solicitud.email().isBlank() && !solicitud.email().equals(usuarioModel.getLogin()))
            emailOcuped = usuarioRepository.existsByLogin(solicitud.email());

        if (emailOcuped) return ResponseEntity.badRequest().body(new Mensaje("El email esta indisponible"));
        LocalDateTime thrityMinutesBefore = LocalDateTime.now().minusMinutes(60);
        var solicitudes = usuarioSolicitudRepository.findByEmailAfterTime(solicitud.email(), thrityMinutesBefore, EnumTypeSolicitudOperation.UPDATE);
        if (solicitudes.size() >= 5)
            return ResponseEntity.badRequest().body(new Mensaje("Fueron intentadas muchas solicitaciones"));

        if (!passwordEncoder.matches(solicitud.contrasena(), usuarioModel.getPassword()))
            return ResponseEntity.badRequest().body(new Mensaje("La contrasena es invalida"));
        var usuarioSolicitud = new UsuarioSolicitudModel(solicitud, usuarioModel, usuarioModel.getPassword(), EnumTypeSolicitudOperation.UPDATE);

        usuarioSolicitudRepository.save(usuarioSolicitud);
        emailService.mandarEmail(usuarioModel.getLogin(), "Cambio de datos del perfil", "Este es tu codigo de verificacion para editar tud datos: \n" + usuarioSolicitud.getId().toString() + "\nNo lo compartas con nadie");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/validar_update")
    public ResponseEntity<Object> vaidateUpdate(@RequestBody @Valid UsuarioDTOUpdateValidation form) {
        var userLogado = myUserComponent.getUserModel();
        var usuarioSolicitud = usuarioSolicitudRepository.findById(form.codigo());
        if (usuarioSolicitud.isEmpty())
            throw new ValidationException("codigo", "El codigo de verificacion es invalido");


        LocalDateTime thyrtyMinutesBefore = LocalDateTime.now().minusMinutes(30);

        if (usuarioSolicitud.get().getCreatedAt().isBefore(thyrtyMinutesBefore)) {
            usuarioSolicitudRepository.delete(usuarioSolicitud.get());
            throw new ValidationException("codigo", "El codigo ha expirado");
        }

        if (!usuarioSolicitud.get().getEmail().equals(userLogado.getLogin()))
            throw new ValidationException("email", "El codigo no le pertenece a este usuario");

        userLogado.updateValues(usuarioSolicitud.get());
        usuarioRepository.save(userLogado);
        return ResponseEntity.noContent().build();
    }
}
