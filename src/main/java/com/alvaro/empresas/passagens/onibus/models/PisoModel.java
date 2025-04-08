package com.alvaro.empresas.passagens.onibus.models;

import java.util.UUID;

import com.alvaro.empresas.passagens.helpers.utils.IntegerListStringUtil;
import com.alvaro.empresas.passagens.models.IEntityStandart;
import com.alvaro.empresas.passagens.onibus.dtos.PisoInputDTO;
import com.alvaro.empresas.passagens.onibus.enums.TipePosicao;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_piso", indexes = @Index(name = "idxtb_piso_fk_idtb_onibus", columnList = "fk_idtb_onibus"))
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AttributeOverride(name = "id ", column = @Column(name = "idtb_piso"))
public class PisoModel extends IEntityStandart {
    @Column(nullable = false)
    private Integer nLinhas;
    @Column(nullable = false)
    private Integer nColunas;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipePosicao distribuicaoFileira;
    @Column(nullable = false)
    private Integer nPiso;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipePosicao inicioContagem;
    @Column(nullable = false)
    private Integer nAssentos;
    @Column(nullable = false)
    private Integer primeiroAssento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_onibus")
    private OnibusModel onibus;
    @Column(name = "fk_idtb_onibus", insertable = false, updatable = false)
    private UUID onibusId;

    private String posicoesBloquedas = "";

    public PisoModel(PisoInputDTO dto, Integer nPiso, Integer primeiroAssento) {
        nAssentos = dto.nAssentos();
        nLinhas = dto.nLinhas();
        nColunas = dto.nColunas();
        distribuicaoFileira = dto.distribuicaoFileira();
        inicioContagem = dto.inicioContagem();
        this.nPiso = nPiso;
        this.primeiroAssento = primeiroAssento;
        this.posicoesBloquedas = IntegerListStringUtil.convertListToString(",", dto.posicoesBloquedas());
    }

    public boolean hasNAssento(Integer nAssento) {
        return nAssento >= primeiroAssento && nAssento <= getUltimoAssento();
    }

    public Integer getUltimoAssento() {
        return nAssentos + primeiroAssento - 1;
    }

    public PisoModel(Integer nLinhas, Integer nColunas, TipePosicao distribuicaoFileira, Integer nPiso,
            TipePosicao inicioContagem, Integer nAssentos, Integer primeiroAssento, OnibusModel onibus) {
        this.nLinhas = nLinhas;
        this.nColunas = nColunas;
        this.distribuicaoFileira = distribuicaoFileira;
        this.nPiso = nPiso;
        this.inicioContagem = inicioContagem;
        this.nAssentos = nAssentos;
        this.primeiroAssento = primeiroAssento;
        this.onibus = onibus;
    }

    public void updateValues(PisoInputDTO dto) {
        nAssentos = dto.nAssentos();
        nLinhas = dto.nLinhas();
        nColunas = dto.nColunas();
        distribuicaoFileira = dto.distribuicaoFileira();
        inicioContagem = dto.distribuicaoFileira();
        this.posicoesBloquedas = IntegerListStringUtil.convertListToString(",", dto.posicoesBloquedas());
    }
}