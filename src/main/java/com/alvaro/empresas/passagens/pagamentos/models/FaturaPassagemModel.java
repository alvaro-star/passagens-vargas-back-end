package com.alvaro.empresas.passagens.pagamentos.models;

import com.alvaro.empresas.passagens.enums.TipoPagamento;
import com.alvaro.empresas.passagens.models.ContatoModel;
import com.alvaro.empresas.passagens.models.PassagemModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(
        name = "tb_fatura_passagem",
        indexes = @Index(name = "idxtb_viagem_fk_idtb_viagem_criado_em", columnList = "fk_idtb_viagem, created_at")
)
@AttributeOverride(name = "id", column = @Column(name = "idtb_fatura_passagem"))
public class FaturaPassagemModel extends IFaturaStandart {
    @Column(nullable = false)
    private BigDecimal desconto;
    @Column(nullable = false)
    private BigDecimal taxaServico;
    @Column(nullable = false, name = "esta_pago")
    private Boolean estaPago;
    @Column(nullable = false, name = "metodo_pagamento")
    @Enumerated(EnumType.STRING)
    private TipoPagamento metodoPagamento;
    private LocalDateTime dataPagamento;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "faturaPassagem")
    private ContatoModel contato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_idtb_cliente")
    @JsonIgnore
    private UsuarioModel cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_idtb_viagem")
    @JsonIgnore
    private ViagemModel viagem;
    @Column(name = "fk_idtb_viagem", updatable = false, insertable = false)
    @JsonIgnore
    private UUID viagemId;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "faturaPassagem")
    @JsonIgnore
    private List<PassagemModel> passagens;

    public void setViagem(ViagemModel viagem) {
        this.viagem = viagem;
        this.viagemId = (viagem != null) ? viagem.getId() : null;
    }

    public FaturaPassagemModel(BigDecimal valorTotal, BigDecimal desconto, BigDecimal taxaServico, Boolean estaPago, TipoPagamento metodoPagamento, ViagemModel viagemModel, LocalDateTime dataPagamento, ContatoModel contato) {
        super(valorTotal);
        this.desconto = desconto;
        this.taxaServico = taxaServico;
        this.estaPago = estaPago;
        this.metodoPagamento = metodoPagamento;
        this.dataPagamento = dataPagamento;
        setViagem(viagemModel);
        this.viagem = viagemModel;
        this.contato = contato;
    }
}