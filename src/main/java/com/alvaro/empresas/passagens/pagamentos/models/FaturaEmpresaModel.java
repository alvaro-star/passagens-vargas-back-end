package com.alvaro.empresas.passagens.pagamentos.models;

import com.alvaro.empresas.passagens.models.EmpresaModel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_fatura_empresa", indexes = @Index(
        name = "idxtb_fatura_empresa_fk_idtb_empresa_inicio_contagem",
        columnList = "fk_idtb_empresa, inicio_contagem"))
@DiscriminatorValue("EMPRESA")
@NoArgsConstructor
public class FaturaEmpresaModel extends IFaturaStandart {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_empresa")
    private EmpresaModel empresa;
    @Column(nullable = false)
    private LocalDateTime inicioContagem;
    @Column(nullable = false)
    private LocalDateTime fimContagem;

    public FaturaEmpresaModel(BigDecimal valorTotal, EmpresaModel empresa, LocalDateTime inicioContagem, LocalDateTime fimContagem) {
        super(valorTotal);
        this.empresa = empresa;
        this.inicioContagem = inicioContagem;
        this.fimContagem = fimContagem;
    }
}