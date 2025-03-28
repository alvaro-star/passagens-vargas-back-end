package com.alvaro.empresas.passagens.onibus.models;

import com.alvaro.empresas.passagens.models.IEntityStandart;
import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoDTOCreate;
import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoDTOUpdate;
import com.alvaro.empresas.passagens.onibus.enums.TipePosicao;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


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

    public PisoModel(PisoDTOCreate dto, Integer nPiso, Integer primeiroAssento) {
        nAssentos = dto.getNColunas() * dto.getNLinhas() - dto.getPosicoesBloquedas().size();
        nLinhas = dto.getNLinhas();
        nColunas = dto.getNColunas();
        distribuicaoFileira = dto.getDistribuicaoFileira();
        inicioContagem = dto.getInicioContagem();
        this.nPiso = nPiso;
        this.primeiroAssento = primeiroAssento;
        StringBuilder str = new StringBuilder();
        for (Integer posicaoBloqueada : dto.getPosicoesBloquedas())
            str.append(posicaoBloqueada).append(",");
        str.deleteCharAt(str.length() - 1);
        this.posicoesBloquedas = str.toString();
    }

    public int[] getPosicionesBloqueadasIntegerList() {
        if (posicoesBloquedas.isBlank()) return new int[0];
        String[] posicoes = this.posicoesBloquedas.split(",");
        int[] posicoesConvertidas = new int[posicoes.length];
        for (int i = 0; i < posicoes.length; i++) {
            posicoesConvertidas[i] = Integer.parseInt(posicoes[i]);
        }
        return posicoesConvertidas;
    }

    public boolean hasNAssento(Integer nAssento) {
        return nAssento >= primeiroAssento && nAssento <= getUltimoAssento();
    }

    public Integer getUltimoAssento() {
        return nAssentos + primeiroAssento - 1;
    }

    public PisoModel(Integer nLinhas, Integer nColunas, TipePosicao distribuicaoFileira, Integer nPiso, TipePosicao inicioContagem, Integer nAssentos, Integer primeiroAssento, OnibusModel onibus) {
        this.nLinhas = nLinhas;
        this.nColunas = nColunas;
        this.distribuicaoFileira = distribuicaoFileira;
        this.nPiso = nPiso;
        this.inicioContagem = inicioContagem;
        this.nAssentos = nAssentos;
        this.primeiroAssento = primeiroAssento;
        this.onibus = onibus;
    }

    public void updateValues(PisoDTOUpdate dto) {
        nAssentos = dto.getNColunas() * dto.getNLinhas() - dto.getPosicoesIndisponiveis().size();
        nLinhas = dto.getNLinhas();
        nColunas = dto.getNColunas();
        distribuicaoFileira = dto.getDistribuicaoFileira();
        inicioContagem = dto.getInicioContagem();

        String palavra = "";
        for (Integer posicaoBloqueada : dto.getPosicoesIndisponiveis())
            palavra = palavra.concat(posicaoBloqueada + ",");
        this.posicoesBloquedas = palavra;
    }
}