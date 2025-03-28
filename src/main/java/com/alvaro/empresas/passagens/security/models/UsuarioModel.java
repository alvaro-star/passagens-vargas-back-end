package com.alvaro.empresas.passagens.security.models;

import com.alvaro.empresas.passagens.models.IEntityStandart;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaPassagemModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Table(name = "tb_usuario")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "idtb_usuario"))
public class UsuarioModel extends IEntityStandart implements UserDetails {
    @Column(unique = true, name = "email", nullable = false)
    private String email;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false)
    private String telefone;
    @Column(nullable = false)
    private String senha;

    private UUID empresaId;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "cliente")
    private List<FaturaPassagemModel> faturasPassagem;

    @NotNull
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_usuario_role",
            joinColumns = @JoinColumn(name = "idtb_usuario", referencedColumnName = "idtb_usuario"),
            inverseJoinColumns = @JoinColumn(name = "idtb_role", referencedColumnName = "idtb_role"))
    private Set<RoleModel> roles = new HashSet<>();


    public UsuarioModel(String email, String nome, String telefone, String senha) {
        this.email = email;
        this.nome = nome;
        this.senha = senha;
        this.telefone = telefone;
    }

    public UsuarioModel(String email, String nome, String telefone, String senha, UUID empresaId) {
        this.email = email;
        this.nome = nome;
        this.telefone = telefone;
        this.senha = senha;
        this.empresaId = empresaId;
    }

    public UsuarioModel(UsuarioSolicitacaoModel usuarioSolicitacaoModel) {
        this.email = usuarioSolicitacaoModel.getEmail();
        this.nome = usuarioSolicitacaoModel.getNome();
        this.telefone = usuarioSolicitacaoModel.getTelefone();
        this.senha = usuarioSolicitacaoModel.getSenha();
        this.empresaId = null;
    }

    public List<String> rolesToListString() {
        return this.roles.stream().map(RoleModel::getAuthority).toList();
    }

    public boolean hasRole(RoleList role) {
        for (RoleModel roleModel : this.roles) {
            if (roleModel.getNome().equals(role))
                return true;
        }
        return false;
    }


    public boolean addRole(RoleModel role) {
        return this.roles.add(role);
    }

    public boolean removeRole(RoleModel role) {
        return this.roles.remove(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void updateValues(UsuarioSolicitacaoModel usuarioSolicitacao) {
        this.email = usuarioSolicitacao.getNewEmail();
        this.telefone = usuarioSolicitacao.getTelefone();
        this.nome = usuarioSolicitacao.getNome();
    }
}