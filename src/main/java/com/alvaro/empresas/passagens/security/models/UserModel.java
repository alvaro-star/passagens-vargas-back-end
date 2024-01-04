package com.alvaro.empresas.passagens.security.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Table(name = "tb_usuario")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserModel implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idtb_usuario")
    private String id;
    @Column(name = "email", nullable = false)
    private String login;
    private String carnet;
    private String nombre;
    private String contrasena;

    @NotNull
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_usuario_role",
            joinColumns = @JoinColumn(name = "idtb_usuario", referencedColumnName = "idtb_usuario"),
            inverseJoinColumns = @JoinColumn(name = "idtb_role", referencedColumnName = "idtb_role"))
    private Set<RoleModel> roles = new HashSet<>();

    public UserModel(String login, String nombre, String carnet, String contrasena, Set<RoleModel> roles) {
        this.login = login;
        this.nombre = nombre;
        this.carnet = carnet;
        this.contrasena = contrasena;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return contrasena;
    }

    @Override
    public String getUsername() {
        return login;
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
}
