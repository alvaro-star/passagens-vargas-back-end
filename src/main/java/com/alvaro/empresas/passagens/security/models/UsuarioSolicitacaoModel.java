package com.alvaro.empresas.passagens.security.models;

import com.alvaro.empresas.passagens.enums.TipoSolicitacao;
import com.alvaro.empresas.passagens.models.IEntityStandart;
import com.alvaro.empresas.passagens.security.dtos.UsuarioDTOUpdate;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(name = "tb_usuario_solicitacao", indexes = @Index(name = "idx_tb_usuario_solicitacao", columnList = "email"))
@Entity
@AttributeOverride(name = "nome", column = @Column(name = "idtb_usuario_solicitacao"))
public class UsuarioSolicitacaoModel extends IEntityStandart {
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "novo_email")
    private String newEmail;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false)
    private String telefone;
    @Column(nullable = false)
    private String senha;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoSolicitacao tipo;

    public UsuarioSolicitacaoModel(String email, String nome, String telefone, String senha, TipoSolicitacao tipo) {
        this.email = email;
        this.nome = nome;
        this.telefone = telefone;
        this.senha = senha;
        this.tipo = tipo;
    }

    public UsuarioSolicitacaoModel(UsuarioDTOUpdate solicitacao, UsuarioModel usuario, String senhaCriptografada, TipoSolicitacao tipo) {
        this.email = usuario.getEmail();
        if (solicitacao.email() == null || solicitacao.email().isBlank()) this.newEmail = usuario.getEmail();
        else this.newEmail = solicitacao.email();

        if (solicitacao.senha() == null || solicitacao.senha().isBlank())
            this.senha = usuario.getSenha();
        else this.senha = senhaCriptografada;

        if (solicitacao.nome() == null || solicitacao.nome().isBlank()) this.nome = usuario.getNome();
        else this.nome = solicitacao.nome();

        if (solicitacao.telefone() == null || solicitacao.telefone().isBlank()) this.telefone = usuario.getTelefone();
        else this.telefone = solicitacao.telefone();
        this.tipo = tipo;
    }
}