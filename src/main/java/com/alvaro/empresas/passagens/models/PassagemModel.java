package com.alvaro.empresas.passagens.models;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.UUID;

import com.alvaro.empresas.passagens.dtos.pasagens.PassagemDTO;
import com.alvaro.empresas.passagens.enums.TipoPagamento;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaPassagemModel;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaReembolsoModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "tb_passagem", indexes = {
        @Index(name = "idxtb_passagem_fk_idtb_preco", columnList = "fk_idtb_preco"),
        @Index(name = "idxtb_passagem_fk_idtb_fatura_passagem", columnList = "fk_idtb_fatura_passagem")
})
@AttributeOverride(name = "id", column = @Column(name = "idtb_passagem"))
public class PassagemModel extends IEntityStandart {

    @Column(nullable = false)
    private Integer nAssento;
    @Column(nullable = false)
    private BigDecimal precoPago;
    @Column(name = "comprado_na_web", nullable = false)
    private Boolean compradoWeb;
    @Column(name = "pago", nullable = false)
    private Boolean estaPago;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_idtb_fatura_reembolso")
    private FaturaReembolsoModel faturaReembolso;
    @Column(name = "fk_idtb_fatura_reembolso", insertable = false, updatable = false)
    private UUID faturaReembolsoId;

    @Column(nullable = false)
    private Boolean emDinheiro;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoPagamento metodoPagamento;

    @Column(nullable = false, length = 9)
    private String cpf;
    @Column(nullable = false, length = 70)
    private String nome;
    @Column(nullable = false)
    private LocalDate nascimento;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_id_saida")
    private ParadaModel saida;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_id_destino")
    private ParadaModel destino;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_idtb_preco")
    private PrecoModel preco;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_fatura_passagem")
    private FaturaPassagemModel faturaPassagem;
    @Column(name = "fk_idtb_fatura_passagem", updatable = false, insertable = false)
    private UUID faturaPassagemId;

    public String getNascimentoString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(nascimento);
    }

    public void setFaturaPassagem(FaturaPassagemModel faturaPassagem) {
        if (faturaPassagem == null) {
            this.metodoPagamento = null;
            this.faturaPassagem = null;
            this.faturaPassagemId = null;
        } else {
            this.faturaPassagem = faturaPassagem;
            this.metodoPagamento = faturaPassagem.getMetodoPagamento();
            this.faturaPassagemId = faturaPassagem.getId();
        }
    }

    public PassagemModel(PassagemDTO passagemDTO, Boolean compradoWeb, BigDecimal precoPago, Boolean estaPago, Boolean emDinheiro, ParadaModel saida, ParadaModel destino, PrecoModel preco, FaturaPassagemModel faturaPassagem) {
        this.nAssento = passagemDTO.nAssento();
        this.cpf = passagemDTO.cpf();
        this.nome = passagemDTO.nome();
        this.nascimento = passagemDTO.nascimento();

        this.precoPago = precoPago;
        this.compradoWeb = compradoWeb;
        this.estaPago = estaPago;
        setFaturaReembolso(null);
        setFaturaPassagem(faturaPassagem);
        this.emDinheiro = emDinheiro;
        this.saida = saida;
        this.destino = destino;
        this.preco = preco;
    }

    public PassagemModel(Integer nAssento, Boolean compradoWeb, BigDecimal precoPago, Boolean estaPago, Boolean emDinheiro, String nome, String cpf, LocalDate nascimento, ParadaModel saida, ParadaModel destino, PrecoModel preco, FaturaPassagemModel faturaPassagem) {
        this.nAssento = nAssento;
        this.compradoWeb = compradoWeb;
        this.estaPago = estaPago;
        this.emDinheiro = emDinheiro;
        this.cpf = cpf;
        this.nome = nome.toUpperCase();
        this.nascimento = nascimento;
        this.precoPago = precoPago;
        this.preco = preco;
        setFaturaReembolso(null);
        setFaturaPassagem(faturaPassagem);
        this.saida = saida;
        this.destino = destino;
    }
}