package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.dtos.pasagens.PasagemDTO;
import com.alvaro.empresas.passagens.enums.TipoPagamento;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaPasagemModel;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaReembolsoModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "tb_pasagem", indexes = {
        @Index(name = "idxtb_pasagem_fk_idtb_preco", columnList = "fk_idtb_preco"),
        @Index(name = "idxtb_pasagem_fk_idtb_fatura_pasagem", columnList = "fk_idtb_fatura_pasagem")
})
@AttributeOverride(name = "id", column = @Column(name = "idtb_pasagem"))
public class PassagemModel extends IEntityStandart {

    @Column(nullable = false)
    private Integer numeroAssento;
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
    private String documento;
    @Column(nullable = false, length = 70)
    private String nome;
    @Column(nullable = false)
    private Date nascimento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_id_saida")
    private ParadaModel saida;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_id_destino")
    private ParadaModel destino;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_idtb_preco")
    private PrecoModel preco;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_fatura_pasagem")
    private FaturaPasagemModel faturaPasagem;
    @Column(name = "fk_idtb_fatura_pasagem", updatable = false, insertable = false)
    private UUID faturaPasagemId;

    public String getNascimentoString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(nascimento);
    }

    public void setFaturaPasagem(FaturaPasagemModel faturaPasagem) {
        if (faturaPasagem == null) {
            this.metodoPagamento = null;
            this.faturaPasagem = null;
            this.faturaPasagemId = null;
        } else {
            this.faturaPasagem = faturaPasagem;
            this.metodoPagamento = faturaPasagem.getMetodoPagamento();
            this.faturaPasagemId = faturaPasagem.getId();
        }
    }

    public PassagemModel(PasagemDTO passagemDTO, Boolean compradoWeb, BigDecimal precoPago, Boolean estaPago, Boolean emDinheiro, ParadaModel saida, ParadaModel destino, PrecoModel preco, FaturaPasagemModel faturaPasagem) {
        this.numeroAssento = passagemDTO.numeroAssento();
        this.documento = passagemDTO.documento();
        this.nome = passagemDTO.nome();
        this.nascimento = passagemDTO.nascimento();

        this.precoPago = precoPago;
        this.compradoWeb = compradoWeb;
        this.estaPago = estaPago;
        this.faturaReembolso = null;
        this.faturaReembolsoId = null;
        this.emDinheiro = emDinheiro;
        setFaturaPasagem(faturaPasagem);
        this.saida = saida;
        this.destino = destino;
        this.preco = preco;
    }

    public PassagemModel(Integer numeroAssento, Boolean compradoWeb, BigDecimal precoPago, Boolean estaPago, Boolean emDinheiro, String nome, String documento, Date nascimento, ParadaModel saida, ParadaModel destino, PrecoModel preco, FaturaPasagemModel faturaPasagem) {
        this.numeroAssento = numeroAssento;
        this.compradoWeb = compradoWeb;
        this.estaPago = estaPago;
        this.emDinheiro = emDinheiro;
        this.documento = documento;
        this.nome = nome.toUpperCase();
        this.nascimento = nascimento;
        this.precoPago = precoPago;
        this.preco = preco;
        this.faturaPasagem = faturaPasagem;
        this.metodoPagamento = faturaPasagem.getMetodoPagamento();
        this.saida = saida;
        this.destino = destino;
        this.faturaReembolso = null;
        this.faturaReembolsoId = null;
    }
}
