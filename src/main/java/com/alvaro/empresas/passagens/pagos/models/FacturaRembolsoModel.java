package com.alvaro.empresas.passagens.pagos.models;


import com.alvaro.empresas.passagens.models.PasajeModel;
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
public class FacturaRembolsoModel extends FacturaAbstractModel {
    @ManyToOne
    @JoinColumn(name = "fk_idtb_factura_pasaje")
    private FacturaPasajeModel facturaPasaje;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "facturaRembolso")
    private PasajeModel pasaje;

    public FacturaRembolsoModel(BigDecimal valorTotal, FacturaPasajeModel facturaPasaje, PasajeModel pasaje) {
        super(valorTotal);
        this.facturaPasaje = facturaPasaje;
        this.pasaje = pasaje;
    }
}
