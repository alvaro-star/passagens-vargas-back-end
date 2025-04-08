package com.alvaro.empresas.passagens.pagamentos.models;


import com.alvaro.empresas.passagens.models.PassagemModel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table
@Entity(name = "tb_fatura_reembolso")
@AttributeOverride(name = "id", column = @Column(name = "idtb_fatura_reembolso"))
public class FaturaReembolsoModel extends IFaturaStandart {
    @ManyToOne
    @JoinColumn(name = "fk_idtb_fatura_passagem")
    private FaturaPassagemModel faturaPassagem;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "faturaReembolso")
    private PassagemModel passagem;

    public FaturaReembolsoModel(BigDecimal valorTotal, FaturaPassagemModel faturaPassagem, PassagemModel passagem) {
        super(valorTotal);
        this.faturaPassagem = faturaPassagem;
        this.passagem = passagem;
    }
}