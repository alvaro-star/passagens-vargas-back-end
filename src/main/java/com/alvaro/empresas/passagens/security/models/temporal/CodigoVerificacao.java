package com.alvaro.empresas.passagens.security.models.temporal;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_codigo", indexes = @Index(name = "idxtb_codigo_email_created_at", columnList = "email, created_at"))
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
