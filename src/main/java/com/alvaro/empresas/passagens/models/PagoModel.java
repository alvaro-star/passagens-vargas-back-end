package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.enums.MetodoPagamentoEnum;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
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
    private BigDecimal valorTotal;
    @Column(nullable = false)
    private BigDecimal descuento;
    @Column(nullable = false)
    private BigDecimal tasaServicio;
    @Column(nullable = false, name = "pagado?")
    private Boolean estaPagado;
    @Column(nullable = false, name = "metodo_pago")
    @Enumerated(EnumType.STRING)
    private MetodoPagamentoEnum metodoPago;
    private LocalDateTime fechaPago;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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

    public PagoModel(BigDecimal valorTotal, BigDecimal descuento, BigDecimal tasaServicio, Boolean estaPagado, MetodoPagamentoEnum metodoPago, ViajeModel viajeModel, LocalDateTime fechaPago, ContactoModel contacto) {
        this.valorTotal = valorTotal;
        this.descuento = descuento;
        this.tasaServicio = tasaServicio;
        this.estaPagado = estaPagado;
        this.metodoPago = metodoPago;
        this.fechaPago = fechaPago;
        this.viaje = viajeModel;
        this.contacto = contacto;
    }
}
