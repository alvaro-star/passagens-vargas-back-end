package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.enums.MetodoPagamentoEnum;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tb_pago")
@NoArgsConstructor
public class PagoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idtb_pagamento")
    private UUID id;
    @Column(nullable = false)
    private Float valorTotal;
    @Column(nullable = false)
    private Float descuento;
    @Column(nullable = false)
    private Float tasaServicio;
    @Column(nullable = false, name = "pagado?")
    private Boolean estaPagado;

    @Column(nullable = false, name = "metodo_pago")
    @Enumerated(EnumType.STRING)
    private MetodoPagamentoEnum metodoPago;

    @Embedded
    private ContactoHelper contacto;

    private LocalDateTime fechaPago;

    private LocalDateTime criatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "fk_idtb_usuario")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UsuarioModel usuario;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "pago")
    private List<PasajeModel> pasajes;

    @PrePersist
    protected void onCreate() {
        criatedAt = LocalDateTime.now();
    }
}
