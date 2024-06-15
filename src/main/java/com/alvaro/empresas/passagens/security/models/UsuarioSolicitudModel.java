package com.alvaro.empresas.passagens.security.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
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
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String telefono;
    @Column(nullable = false)
    private String contrasena;
    @Column(nullable = false)
    private UUID codigoVerificacion;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UsuarioSolicitudModel(String email, String nombre, String telefono, String contrasena) {
        this.email = email;
        this.nombre = nombre;
        this.telefono = telefono;
        this.contrasena = contrasena;
    }
}
