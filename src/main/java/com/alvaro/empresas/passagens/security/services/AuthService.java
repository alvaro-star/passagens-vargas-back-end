package com.alvaro.empresas.passagens.security.services;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import com.alvaro.empresas.passagens.enums.TipoSolicitacao;
import com.alvaro.empresas.passagens.services.EmailService;
import com.alvaro.empresas.passagens.security.dtos.LoginDTO;
import com.alvaro.empresas.passagens.security.dtos.RegisterDTO;
import com.alvaro.empresas.passagens.security.dtos.TokenDTO;
import com.alvaro.empresas.passagens.security.dtos.ValidadorDTO;
import com.alvaro.empresas.passagens.security.dtos.senha.SenhaForm;
import com.alvaro.empresas.passagens.security.dtos.senha.NovaSenhaSolicitacao;
import com.alvaro.empresas.passagens.security.models.RoleList;
import com.alvaro.empresas.passagens.security.models.RoleModel;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import com.alvaro.empresas.passagens.security.models.UsuarioSolicitacaoModel;
import com.alvaro.empresas.passagens.security.models.temporal.CodigoVerificacao;
import com.alvaro.empresas.passagens.security.repositories.CodigoRepository;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import com.alvaro.empresas.passagens.security.repositories.UsuarioSolicitacaoRepository;
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
    private UsuarioSolicitacaoRepository usuarioSolicitacaoRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CodigoRepository codigoRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public TokenDTO login(LoginDTO dto) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(dto.email(), dto.senha());
            var auth = authenticationManager.authenticate(usernamePassword);
            var token = tokenService.generateToken((UsuarioModel) auth.getPrincipal());
            var refreshToken = tokenService.generateRefreshToken((UsuarioModel) auth.getPrincipal());
            return new TokenDTO(token, refreshToken);
        } catch (Exception e) {
            throw new RestRuntimeException("As credenciais são inválidas");
        }
    }

    public TokenDTO refresh(String refreshToken) {
        if (refreshToken == null)
            throw new RestRuntimeException("O token é nulo");
        if (refreshToken.startsWith("Bearer "))
            refreshToken = refreshToken.replace("Bearer ", "");

        String usuario = tokenService.validateToken(refreshToken);
        var usuarioModel = usuarioRepository.findByEmail(usuario);
        if (usuarioModel.isEmpty()) throw new RestRuntimeException("Usuário inválido");

        String accessToken = tokenService.generateToken(usuarioModel.get());
        return new TokenDTO(accessToken, null);
    }

    public void registrar(RegisterDTO registerDTO) {
        var usuarioLogin = usuarioRepository.findByEmail(registerDTO.email());
        if (usuarioLogin.isPresent())
            throw new RestRuntimeException("Já existe um usuário registrado");

        LocalDateTime inicioDoDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(1);
        var solicitacoes = usuarioSolicitacaoRepository.findByEmailAfterTime(registerDTO.email(), inicioDoDia, TipoSolicitacao.CREATE);

        if (solicitacoes.size() > 5)
            throw new RestRuntimeException("Já houve muitas tentativas com este e-mail hoje");

        var usuario = SecurityContextHolder.getContext().getAuthentication();
        boolean logado;

        logado = !(usuario == null || usuario.getName().equals("anonymousUser"));
        if (logado)
            throw new RestRuntimeException(HttpStatus.UNAUTHORIZED, "Há um usuário que já iniciou sessão");

        String senhaCriptografada = this.passwordEncoder.encode(registerDTO.senha());
        UsuarioSolicitacaoModel novoUsuario = new UsuarioSolicitacaoModel(registerDTO.email(), registerDTO.nome(), registerDTO.telefone(), senhaCriptografada, TipoSolicitacao.CREATE);
        usuarioSolicitacaoRepository.save(novoUsuario);
        boolean emailEnviado;
        emailEnviado = emailService.mandarEmail(novoUsuario.getEmail(), "Código de Verificação", novoUsuario.getNome() + ", este é o seu código de verificação para sua conta no aplicativo: \n" + novoUsuario.getId().toString() + "\nNão compartilhe com ninguém");
        if (!emailEnviado)
            throw new RestRuntimeException(emailService.messageEmailNotSended);
    }

    public void verificarRegistro(ValidadorDTO validadorDTO) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime meiaHoraAtras = agora.minus(Duration.ofMinutes(30));
        var solicitacoes = usuarioSolicitacaoRepository.findByEmailAfterTime(validadorDTO.email(), meiaHoraAtras, TipoSolicitacao.CREATE);
        var usuario = usuarioRepository.findByEmail(validadorDTO.email());
        if (usuario.isPresent())
            throw new RestRuntimeException("O usuário já está registrado");
        if (solicitacoes.isEmpty())
            throw new RestRuntimeException("Não há solicitações recentes");
        if (!solicitacoes.get(0).getId().equals(validadorDTO.codigo()))
            throw new RestRuntimeException("O código de verificação está incorreto");

        usuarioSolicitacaoRepository.deleteByEmailBeforeTime(validadorDTO.email(), meiaHoraAtras);
        var novoUsuario = new UsuarioModel(solicitacoes.get(0));
        Set<RoleModel> roles = new HashSet<>();
        var roleCliente = roleService.getByRoleName(RoleList.ROLE_CLIENTE);
        roles.add(roleCliente);
        novoUsuario.setRoles(roles);
        usuarioRepository.save(novoUsuario);
    }

    public void obterCodigoParaRedefinirSenha(NovaSenhaSolicitacao solicitacao) {
        var usuario = usuarioRepository.findByEmail(solicitacao.email());
        if (usuario.isEmpty()) throw new RestRuntimeException("Informe um e-mail válido");
        LocalDateTime sessentaMinutosAntes = LocalDateTime.now().minusMinutes(60);
        List<CodigoVerificacao> codigos = codigoRepository.findByEmailAfterDate(solicitacao.email(), sessentaMinutosAntes);
        if (codigos.size() >= 5)
            throw new RestRuntimeException("Muitas solicitações foram feitas");

        CodigoVerificacao codigo = new CodigoVerificacao();
        codigo.setEmail(solicitacao.email());
        codigoRepository.save(codigo);
        var emailEnviado = emailService.mandarEmail(solicitacao.email(), "Alteração de Senha", "Este é o seu código de verificação para alterar sua senha: \n" + codigo.getId() + "\nNão compartilhe com ninguém");
        if (!emailEnviado)
            throw new RestRuntimeException(emailService.messageEmailNotSended);
    }

    public void validarSenha(SenhaForm formulario) {
        var codigo = codigoRepository.findById(formulario.codigo());
        if (codigo.isEmpty()) throw new ValidationException("codigo", "O código de verificação é inválido");

        LocalDateTime trintaMinutosAntes = LocalDateTime.now().minusMinutes(30);

        if (codigo.get().getCreatedAt().isBefore(trintaMinutosAntes)) {
            codigoRepository.delete(codigo.get());
            throw new ValidationException("codigo", "O código expirou");
        }

        if (!codigo.get().getEmail().equals(formulario.email()))
            throw new ValidationException("email", "O código não pertence a este usuário");

        String senhaCriptografada = this.passwordEncoder.encode(formulario.senha());
        var usuario = usuarioRepository.findByEmail(formulario.email());
        if (usuario.isEmpty()) throw new ValidationException("email", "O usuário não existe");
        usuario.get().setSenha(senhaCriptografada);

        usuarioRepository.save(usuario.get());
        codigoRepository.deleteAllBeforeTime(formulario.email(), LocalDateTime.now());
    }
}

