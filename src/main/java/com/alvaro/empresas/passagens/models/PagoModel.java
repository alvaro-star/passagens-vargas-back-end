package com.alvaro.empresas.passagens.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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
    private Float valor;
    private Float descuento;
    private Float tasaServicio;
    @Column(nullable = false, name = "pagado?")
    private Boolean estaPagado = false;

    private LocalDateTime fechaPago;

    @PrePersist
    protected void onCreate() {
        fechaPago = LocalDateTime.now();
    }
}
