package com.alvaro.empresas.passagens.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tb_pagamentos")
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

    @Embedded
    private ContactoHelper contacto;

    private LocalDateTime fechaPago;

    private LocalDateTime criatedAt;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "pago")
    private List<PasajeModel> pasajes;

    @PrePersist
    protected void onCreate() {
        criatedAt = LocalDateTime.now();
    }
}
