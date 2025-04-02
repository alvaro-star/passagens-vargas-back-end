package com.alvaro.empresas.passagens.security.resource;

import com.alvaro.empresas.passagens.helpers.Mensagem;
import com.alvaro.empresas.passagens.security.dtos.LoginDTO;
import com.alvaro.empresas.passagens.security.dtos.RegisterDTO;
import com.alvaro.empresas.passagens.security.dtos.TokenDTO;
import com.alvaro.empresas.passagens.security.dtos.ValidadorDTO;
import com.alvaro.empresas.passagens.security.dtos.senha.SenhaForm;
import com.alvaro.empresas.passagens.security.dtos.senha.NovaSenhaSolicitacao;
import com.alvaro.empresas.passagens.security.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("auth")
public class AuthResource {
    @Autowired
    private AuthService authService;

    @PostMapping("login")
    @ResponseStatus(HttpStatus.OK)
    public TokenDTO login(@RequestBody @Valid LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }

    @PostMapping("refresh")
    @ResponseStatus(HttpStatus.OK)
    public TokenDTO refresh(@RequestBody Map<String, String> request) {
        return authService.refresh(request.get("refreshToken"));
    }

    // É necessário criar uma rotina para o registro de passagens, uma em que se enviem emails com o código de verificação
    @PostMapping("register")
    @ResponseStatus(HttpStatus.OK)
    public Mensagem register(@RequestBody @Valid RegisterDTO registerDTO) {
        authService.registrar(registerDTO);
        return new Mensagem("Verifique o código de segurança");
    }

    @PostMapping("validar")
    @ResponseStatus(HttpStatus.CREATED)
    public Mensagem verificarRegistro(@RequestBody ValidadorDTO validadorDTO) {
        authService.verificarRegistro(validadorDTO);
        return new Mensagem("Usuário registrado com sucesso");
    }

    @PostMapping("forget_password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void getCodigoParaRecuperarSenha(@RequestBody @Valid NovaSenhaSolicitacao solicitacao) {
        authService.obterCodigoParaRedefinirSenha(solicitacao);
    }

    @PutMapping("reset_password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void validarSenha(@RequestBody @Valid SenhaForm form) {
        authService.validarSenha(form);
    }
}