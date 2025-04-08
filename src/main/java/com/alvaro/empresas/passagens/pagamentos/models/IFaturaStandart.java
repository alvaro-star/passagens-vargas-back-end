package com.alvaro.empresas.passagens.pagamentos.models;

import java.math.BigDecimal;

import com.alvaro.empresas.passagens.models.IEntityStandart;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@MappedSuperclass
public abstract class IFaturaStandart extends IEntityStandart {
    @Column(nullable = false)
    private BigDecimal valorTotal;

    public IFaturaStandart(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}
