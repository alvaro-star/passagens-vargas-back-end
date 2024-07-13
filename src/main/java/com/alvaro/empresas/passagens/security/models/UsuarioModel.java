package com.alvaro.empresas.passagens.security.models;

import com.alvaro.empresas.passagens.models.FacturaPasajeModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
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
    @Column(unique = true, name = "email", nullable = false)
    private String login;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String telefono;
    @Column(nullable = false)
    private String contrasena;
    private UUID idEmpresa;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "cliente")
    private List<FacturaPasajeModel> facturasPasaje;

    @NotNull
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_usuario_role",
            joinColumns = @JoinColumn(name = "idtb_usuario", referencedColumnName = "idtb_usuario"),
            inverseJoinColumns = @JoinColumn(name = "idtb_role", referencedColumnName = "idtb_role"))
    private Set<RoleModel> roles = new HashSet<>();

    public UsuarioModel(String login, String nombre, String telefono, String contrasena) {
        this.login = login;
        this.nombre = nombre;
        this.contrasena = contrasena;
        this.telefono = telefono;
    }

    public UsuarioModel(String login, String nombre, String telefono, String contrasena, UUID idEmpresa) {
        this.login = login;
        this.nombre = nombre;
        this.telefono = telefono;
        this.contrasena = contrasena;
        this.idEmpresa = idEmpresa;
    }

    public UsuarioModel(UsuarioSolicitudModel usuarioSolicitudModel) {
        this.login = usuarioSolicitudModel.getEmail();
        this.nombre = usuarioSolicitudModel.getNombre();
        this.telefono = usuarioSolicitudModel.getTelefono();
        this.contrasena = usuarioSolicitudModel.getContrasena();
        this.idEmpresa = null;
    }


    public boolean hasRole(String role) {
        for (RoleModel roleModel : this.roles) {
            if (roleModel.getAuthority().equals(role))
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
