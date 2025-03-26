package com.alvaro.empresas.passagens.pagamentos.models;

import com.alvaro.empresas.passagens.enums.TipoPagamento;
import com.alvaro.empresas.passagens.models.ContatoModel;
import com.alvaro.empresas.passagens.models.PassagemModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tb_factura_pasagem", indexes = @Index(name = "idxtb_viaje_fk_idtb_viaje_created_at", columnList = "fk_idtb_viaje, created_at"))
@DiscriminatorValue("PASAJE")
public class FaturaPasagemModel extends IFaturaStandart {
    @Column(nullable = false)
    private BigDecimal desconto;
    @Column(nullable = false)
    private BigDecimal taxaServico;
    @Column(nullable = false, name = "pagamento?")
    private Boolean estaPago;
    @Column(nullable = false, name = "metodo_pagamento")
    @Enumerated(EnumType.STRING)
    private TipoPagamento metodoPagamento;
    private LocalDateTime dataPagamento;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "faturaPasagem")
    private ContatoModel contato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_idtb_cliente")
    private UsuarioModel cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_idtb_viaje")
    private ViagemModel viagem;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "faturaPasagem")
    private List<PassagemModel> passagens;

    public FaturaPasagemModel(BigDecimal valorTotal, BigDecimal desconto, BigDecimal taxaServico, Boolean estaPago, TipoPagamento metodoPagamento, ViagemModel viagemModel, LocalDateTime dataPagamento, ContatoModel contato) {
        super(valorTotal);
        this.desconto = desconto;
        this.taxaServico = taxaServico;
        this.estaPago = estaPago;
        this.metodoPagamento = metodoPagamento;
        this.dataPagamento = dataPagamento;
        this.viagem = viagemModel;
        this.contato = contato;
    }
}
