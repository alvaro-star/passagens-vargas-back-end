package com.alvaro.empresas.passagens.security.models;

import com.alvaro.empresas.passagens.models.PagoModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Table(name = "tb_usuario")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class UsuarioModel implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idtb_usuario")
    private UUID id;
    @Column(name = "email", nullable = false)
    private String login;
    @Column(nullable = false)
    private String telefono;
    private String nombre;
    private String contrasena;

    private UUID idEmpresa;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "usuario")
    private List<PagoModel> pagos;

    @NotNull
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_usuario_role",
            joinColumns = @JoinColumn(name = "idtb_usuario", referencedColumnName = "idtb_usuario"),
            inverseJoinColumns = @JoinColumn(name = "idtb_role", referencedColumnName = "idtb_role"))
    private Set<RoleModel> roles = new HashSet<>();

    public UsuarioModel(String login, String nombre, String contrasena) {
        this.login = login;
        this.nombre = nombre;
        this.contrasena = contrasena;
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
