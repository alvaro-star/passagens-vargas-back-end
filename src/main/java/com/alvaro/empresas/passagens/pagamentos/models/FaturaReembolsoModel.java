package com.alvaro.empresas.passagens.pagamentos.models;


import com.alvaro.empresas.passagens.models.PassagemModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Table
@Entity
@Setter
@Getter
@NoArgsConstructor
@DiscriminatorValue("REMBOLSO")
public class FaturaReembolsoModel extends IFaturaStandart {
    @ManyToOne
    @JoinColumn(name = "fk_idtb_factura_pasaje")
    private FaturaPasagemModel facturaPasaje;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "faturaReembolso")
    private PassagemModel pasaje;

    public FaturaReembolsoModel(BigDecimal valorTotal, FaturaPasagemModel facturaPasaje, PassagemModel pasaje) {
        super(valorTotal);
        this.facturaPasaje = facturaPasaje;
        this.pasaje = pasaje;
    }
}
