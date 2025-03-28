package com.alvaro.empresas.passagens.pagamentos.models;

import com.alvaro.empresas.passagens.enums.TipoPagamento;
import com.alvaro.empresas.passagens.models.ContatoModel;
import com.alvaro.empresas.passagens.models.PassagemModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@Table(name = "tb_fatura_passagem", indexes = @Index(name = "idxtb_viagem_fk_idtb_viagem_criado_em", columnList = "fk_idtb_viagem, created_at"))
@DiscriminatorValue("PASSAGEM")
public class FaturaPassagemModel extends IFaturaStandart {
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
    @JoinColumn(name = "fk_idtb_viagem")
    private ViagemModel viagem;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "faturaPasagem")
    private List<PassagemModel> passagens;

    public FaturaPassagemModel(BigDecimal valorTotal, BigDecimal desconto, BigDecimal taxaServico, Boolean estaPago, TipoPagamento metodoPagamento, ViagemModel viagemModel, LocalDateTime dataPagamento, ContatoModel contato) {
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