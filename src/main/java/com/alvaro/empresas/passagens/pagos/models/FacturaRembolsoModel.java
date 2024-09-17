package com.alvaro.empresas.passagens.pagos.models;


import com.alvaro.empresas.passagens.models.PasajeModel;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class FacturaRembolsoModel extends FacturaModel {
    @ManyToOne
    @JoinColumn(name = "fk_idtb_factura_pasaje")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private FacturaPasajeModel facturaPasaje;
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "facturaRembolso")
    private PasajeModel pasaje;

    public FacturaRembolsoModel(BigDecimal valorTotal, FacturaPasajeModel facturaPasaje, PasajeModel pasaje) {
        super(valorTotal);
        this.facturaPasaje = facturaPasaje;
        this.pasaje = pasaje;
    }
}
