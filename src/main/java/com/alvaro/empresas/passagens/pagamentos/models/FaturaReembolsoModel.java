package com.alvaro.empresas.passagens.pagamentos.models;


import com.alvaro.empresas.passagens.models.PassagemModel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Table
@Entity(name = "tb_fatura_reembolso")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@DiscriminatorValue("REEMBOLSO")
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