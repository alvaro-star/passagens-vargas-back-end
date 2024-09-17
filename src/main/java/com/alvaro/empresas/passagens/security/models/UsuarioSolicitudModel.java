package com.alvaro.empresas.passagens.security.models;

import com.alvaro.empresas.passagens.enums.EnumTypeSolicitudOperation;
import com.alvaro.empresas.passagens.security.dtos.UsuarioDTOUpdate;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "tb_usuario_solicitud", indexes = @Index(name = "idx_tb_usuario_solicitud", columnList = "email"))
@Entity
public class UsuarioSolicitudModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idtb_usuario_solicitud")
    private UUID id;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "new_email")
    private String newEmail;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String telefono;
    @Column(nullable = false)
    private String contrasena;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnumTypeSolicitudOperation tipo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UsuarioSolicitudModel(String email, String nombre, String telefono, String contrasena, EnumTypeSolicitudOperation tipo) {
        this.email = email;
        this.nombre = nombre;
        this.telefono = telefono;
        this.contrasena = contrasena;
        this.tipo = tipo;
    }

    public UsuarioSolicitudModel(UsuarioDTOUpdate solicitud, UsuarioModel usuario, String passwordEncripted, EnumTypeSolicitudOperation tipo) {
        this.email = usuario.getLogin();
        if (solicitud.email() == null || solicitud.email().isBlank()) this.newEmail = usuario.getLogin();
        else this.newEmail = solicitud.email();

        if (solicitud.contrasena() == null || solicitud.contrasena().isBlank())
            this.contrasena = usuario.getContrasena();
        else this.contrasena = passwordEncripted;

        if (solicitud.nombre() == null || solicitud.nombre().isBlank()) this.nombre = usuario.getNombre();
        else this.nombre = solicitud.nombre();

        if (solicitud.telefono() == null || solicitud.telefono().isBlank()) this.telefono = usuario.getTelefono();
        else this.telefono = solicitud.telefono();
        this.tipo = tipo;
    }
}
