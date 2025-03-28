package com.alvaro.empresas.passagens.security.models.temporal;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "tb_codigo", indexes = @Index(name = "idxtb_viaje_email_created_at", columnList = "email, created_at"))
public class CodigoVerificacao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idtb_codigo")
    private UUID id;
    private String email;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
