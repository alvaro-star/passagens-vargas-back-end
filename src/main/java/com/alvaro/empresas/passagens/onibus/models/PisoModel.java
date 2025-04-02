package com.alvaro.empresas.passagens.onibus.models;

import com.alvaro.empresas.passagens.interfaces.IEntityStandart;
import com.alvaro.empresas.passagens.onibus.dtos.PisoCreateDTO;
import com.alvaro.empresas.passagens.onibus.dtos.PisoUpdateDTO;
import com.alvaro.empresas.passagens.onibus.enums.TipePosicao;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
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

    public PisoModel(PisoCreateDTO dto, Integer nPiso, Integer primeiroAssento) {
        nAssentos = dto.nAssentosDisponiveis();
        nLinhas = dto.nLinhas();
        nColunas = dto.nColunas();
        distribuicaoFileira = dto.distribuicaoFileira();
        inicioContagem = dto.inicioContagem();
        this.nPiso = nPiso;
        this.primeiroAssento = primeiroAssento;
        this.posicoesBloquedas = joinString(",", dto.posicoesBloquedas());
    }

    private String joinString(String delimiter, List<Integer> numbers) {
        StringBuilder str = new StringBuilder();
        numbers.forEach(number -> str.append(number).append(delimiter));
        if (numbers.size() > 1) str.deleteCharAt(str.length() - 1);
        return str.toString();
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

    public void updateValues(PisoUpdateDTO dto) {
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