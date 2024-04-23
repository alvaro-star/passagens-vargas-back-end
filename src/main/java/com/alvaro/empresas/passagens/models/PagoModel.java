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
    private LocalDateTime fechaPago;

    private LocalDateTime criatedAt;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "pago")
    private ContactoModel contacto;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "fk_idtb_cliente")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UsuarioModel cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_viaje")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ViajeModel viaje;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "pago")
    private List<PasajeModel> pasajes;

    public PagoModel(Float valorTotal, Float descuento, Float tasaServicio, Boolean estaPagado, MetodoPagamentoEnum metodoPago, ViajeModel viajeModel, LocalDateTime fechaPago) {
        this.valorTotal = valorTotal;
        this.descuento = descuento;
        this.tasaServicio = tasaServicio;
        this.estaPagado = estaPagado;
        this.metodoPago = metodoPago;
        this.fechaPago = fechaPago;
        this.viaje = viajeModel;
    }

    @PrePersist
    protected void onCreate() {
        criatedAt = LocalDateTime.now();
    }
}
