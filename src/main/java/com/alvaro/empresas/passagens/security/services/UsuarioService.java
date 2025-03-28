package com.alvaro.empresas.passagens.security.services;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.ValidationException;
import com.alvaro.empresas.passagens.enums.TipoSolicitacao;
import com.alvaro.empresas.passagens.helpers.beans.UserLoguedComponent;
import com.alvaro.empresas.passagens.helpers.services.EmailService;
import com.alvaro.empresas.passagens.security.dtos.UsuarioDTOUpdate;
import com.alvaro.empresas.passagens.security.dtos.UsuarioDTOUpdateValidation;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import com.alvaro.empresas.passagens.security.models.UsuarioSolicitacaoModel;
import com.alvaro.empresas.passagens.security.repositories.UsuarioRepository;
import com.alvaro.empresas.passagens.security.repositories.UsuarioSolicitacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioSolicitacaoRepository usuarioSolicitacaoRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserLoguedComponent userLoguedComponent;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioModel save(UsuarioModel usuarioModel) {
        return usuarioRepository.save(usuarioModel);
    }

    public void updateProfile(UsuarioDTOUpdate solicitacao) {
        var usuarioModel = userLoguedComponent.getUserModel();
        boolean emailUsado = false;

        if (solicitacao.email() != null && !solicitacao.email().isBlank() && !solicitacao.email().equals(usuarioModel.getEmail()))
            emailUsado = usuarioRepository.existsByEmail(solicitacao.email());

        if (emailUsado) throw new RestRuntimeException("O e-mail está indisponível");
        LocalDateTime trintaMinutosAntes = LocalDateTime.now().minusMinutes(60);

        var solicitacoes = usuarioSolicitacaoRepository.findByEmailAfterTime(solicitacao.email(), trintaMinutosAntes, TipoSolicitacao.UPDATE);
        if (solicitacoes.size() >= 5)
            throw new RestRuntimeException("Foram feitas muitas tentativas");

        if (!passwordEncoder.matches(solicitacao.senha(), usuarioModel.getPassword()))
            throw new RestRuntimeException("A senha é inválida");

        var usuarioSolicitacao = new UsuarioSolicitacaoModel(solicitacao, usuarioModel, usuarioModel.getPassword(), TipoSolicitacao.UPDATE);
        usuarioSolicitacaoRepository.save(usuarioSolicitacao);

        var emailEnviado = emailService.mandarEmail(usuarioModel.getEmail(), "Mudança de dados do perfil", "Este é seu código de verificação para editar seus dados: \n" + usuarioSolicitacao.getId().toString() + "\nNão compartilhe com ninguém");
        if (!emailEnviado) throw new RestRuntimeException(emailService.messageEmailNotSended);
    }

    public void validateUpdate(UsuarioDTOUpdateValidation formulario) {
        var usuarioLogado = userLoguedComponent.getUserModel();
        var usuarioSolicitacao = usuarioSolicitacaoRepository.findById(formulario.codigo());
        if (usuarioSolicitacao.isEmpty())
            throw new ValidationException("código", "O código de verificação é inválido");
        LocalDateTime trintaMinutosAntes = LocalDateTime.now().minusMinutes(30);

        if (usuarioSolicitacao.get().getCreatedAt().isBefore(trintaMinutosAntes)) {
            usuarioSolicitacaoRepository.delete(usuarioSolicitacao.get());
            throw new ValidationException("código", "O código expirou");
        }

        if (!usuarioSolicitacao.get().getEmail().equals(usuarioLogado.getEmail()))
            throw new ValidationException("email", "O código não pertence a este usuário");

        usuarioLogado.updateValues(usuarioSolicitacao.get());
        usuarioRepository.save(usuarioLogado);
    }
}
