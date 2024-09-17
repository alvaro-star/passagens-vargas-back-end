package com.alvaro.empresas.passagens.pagos.models;

import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "tb_factura_empresa", indexes = @Index(
        name = "idxtb_factura_empresa_fk_idtb_empresa_inicio_conteo",
        columnList = "fk_idtb_empresa, inicio_conteo"))
@DiscriminatorValue("EMPRESA")
@NoArgsConstructor
public class FacturaEmpresaModel extends FacturaModel {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_empresa")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private EmpresaModel empresa;
    @Column(nullable = false)
    private LocalDateTime inicioConteo;
    @Column(nullable = false)
    private LocalDateTime finConteo;

    public FacturaEmpresaModel(BigDecimal valorTotal, EmpresaModel empresa, LocalDateTime inicioConteo, LocalDateTime finConteo) {
        super(valorTotal);
        this.empresa = empresa;
        this.inicioConteo = inicioConteo;
        this.finConteo = finConteo;
    }
}
