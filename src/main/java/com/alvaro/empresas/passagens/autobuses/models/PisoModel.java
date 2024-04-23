package com.alvaro.empresas.passagens.autobuses.models;

import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.pisos.PisoDTOUpdate;
import com.alvaro.empresas.passagens.autobuses.enums.EnumPosicao;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tb_piso")
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
    private EnumPosicao distribuicaoFileira;
    @Column(nullable = false)
    private Integer nPiso;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnumPosicao inicioContagem;
    @Column(nullable = false)
    private Integer nSillas;
    @Column(nullable = false)
    private Integer primeraSilla;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_autobus")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private AutobusModel autobus;

    private String posicionesBloqueadas = "";

    public PisoModel(PisoDTO dto, Integer nPiso, Integer primeraSilla) {
        nSillas = dto.getNColunas() * dto.getNLinhas() - dto.getPosicoesIndisponiveis().size();
        nLinhas = dto.getNLinhas();
        nColunas = dto.getNColunas();
        distribuicaoFileira = dto.getDistribuicaoFileira();
        inicioContagem = dto.getInicioContagem();
        this.nPiso = nPiso;
        this.primeraSilla = primeraSilla;
        String palavra = "";
        for (Integer posicionBloqueada : dto.getPosicoesIndisponiveis())
            palavra = palavra.concat(posicionBloqueada + ",");
        this.posicionesBloqueadas = palavra;
    }

    public PisoModel(Integer nLinhas, Integer nColunas, EnumPosicao distribuicaoFileira, Integer nPiso, EnumPosicao inicioContagem, Integer nSillas, Integer primeraSilla, AutobusModel autobus) {
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
