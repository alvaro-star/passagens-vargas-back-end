package com.alvaro.empresas.passagens.security.resource;

import com.alvaro.empresas.passagens.helpers.Mensaje;
import com.alvaro.empresas.passagens.security.dtos.LoginDto;
import com.alvaro.empresas.passagens.security.dtos.RegisterDto;
import com.alvaro.empresas.passagens.security.dtos.ValidadorDTO;
import com.alvaro.empresas.passagens.security.dtos.password.PasswordForm;
import com.alvaro.empresas.passagens.security.dtos.password.SolicitudNewPassword;
import com.alvaro.empresas.passagens.security.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthResource {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid LoginDto loginDto) {
        return ResponseEntity.ok(authService.login(loginDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(authService.refresh(request.get("refreshToken")));
    }

    //Es necessario crear una rutina para el registro de pasajes, una en el que se envien email con el codigo de verificacion
    @PostMapping("/register")
    public ResponseEntity<Mensaje> register(@RequestBody @Valid RegisterDto registerDto) {
        authService.register(registerDto);
        return ResponseEntity.ok(new Mensaje("Verifique el codigo de seguridad"));
    }

    @PostMapping("/validar")
    public ResponseEntity<Mensaje> verificarRegistro(@RequestBody ValidadorDTO validadorDTO) {
        authService.verificarRegistro(validadorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Mensaje("Usuario registrado con exito"));
    }

    @PostMapping("/forget_password")
    public ResponseEntity<Object> getCodigoToRestorePassword(@RequestBody @Valid SolicitudNewPassword solicitud) {
        authService.getCodigoToRestorePassword(solicitud);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reset_password")
    public ResponseEntity<Object> validarPassword(@RequestBody @Valid PasswordForm form) {
        authService.validarPassword(form);
        return ResponseEntity.noContent().build();
    }
}
