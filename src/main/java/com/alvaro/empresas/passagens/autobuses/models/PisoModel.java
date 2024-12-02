package com.alvaro.empresas.passagens.autobuses.models;

import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOCreate;
import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.enums.TypePosicao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "tb_piso", indexes = @Index(name = "idxtb_piso_fk_idtb_autobus", columnList = "fk_idtb_autobus"))
@Getter
@Setter
@NoArgsConstructor
public class PisoModel {
    @Id
    @Column(name = "idtb_piso")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer nLinhas;
    @Column(nullable = false)
    private Integer nColunas;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TypePosicao distribuicaoFileira;
    @Column(nullable = false)
    private Integer nPiso;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TypePosicao inicioContagem;
    @Column(nullable = false)
    private Integer nSillas;
    @Column(nullable = false)
    private Integer primeraSilla;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_autobus")
    private AutobusModel autobus;
    @Column(name = "fk_idtb_autobus", insertable = false, updatable = false)
    private Integer autobusId;

    private String posicionesBloqueadas = "";

    public PisoModel(PisoDTOCreate dto, Integer nPiso, Integer primeraSilla) {
        nSillas = dto.getNColunas() * dto.getNLinhas() - dto.getPosicionesBloqueadas().size();
        nLinhas = dto.getNLinhas();
        nColunas = dto.getNColunas();
        distribuicaoFileira = dto.getDistribuicaoFileira();
        inicioContagem = dto.getInicioContagem();
        this.nPiso = nPiso;
        this.primeraSilla = primeraSilla;
        StringBuilder str = new StringBuilder();
        for (Integer posicionBloqueada : dto.getPosicionesBloqueadas())
            str.append(posicionBloqueada).append(",");
        str.deleteCharAt(str.length() - 1);
        this.posicionesBloqueadas = str.toString();
    }

    public int[] getPosicionesBloqueadasIntegerList() {
        if (posicionesBloqueadas.isBlank()) return new int[0];
        String[] posiciones = this.posicionesBloqueadas.split(",");
        int[] posicionesConvert = new int[posiciones.length];
        for (int i = 0; i < posiciones.length; i++) {
            posicionesConvert[i] = Integer.parseInt(posiciones[i]);
        }
        return posicionesConvert;
    }

    public boolean hasNSilla(Integer nSilla) {
        return nSilla >= primeraSilla && nSilla <= getUltimaSilla();
    }

    public Integer getUltimaSilla() {
        return nSillas + primeraSilla - 1;
    }

    public PisoModel(Integer nLinhas, Integer nColunas, TypePosicao distribuicaoFileira, Integer nPiso, TypePosicao inicioContagem, Integer nSillas, Integer primeraSilla, AutobusModel autobus) {
        this.nLinhas = nLinhas;
        this.nColunas = nColunas;
        this.distribuicaoFileira = distribuicaoFileira;
        this.nPiso = nPiso;
        this.inicioContagem = inicioContagem;
        this.nSillas = nSillas;
        this.primeraSilla = primeraSilla;
        this.autobus = autobus;
    }

    public void updateValues(PisoDTOUpdate dto) {
        nSillas = dto.getNColunas() * dto.getNLinhas() - dto.getPosicoesIndisponiveis().size();
        nLinhas = dto.getNLinhas();
        nColunas = dto.getNColunas();
        distribuicaoFileira = dto.getDistribuicaoFileira();
        inicioContagem = dto.getInicioContagem();

        String palavra = "";
        for (Integer posicionBloqueada : dto.getPosicoesIndisponiveis())
            palavra = palavra.concat(posicionBloqueada + ",");
        this.posicionesBloqueadas = palavra;
    }
}
