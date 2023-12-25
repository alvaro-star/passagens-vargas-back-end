package com.alvaro.empresas.passagens.autobuses.models;

import com.alvaro.empresas.passagens.autobuses.dtos.PisoDTO;
import com.alvaro.empresas.passagens.autobuses.enums.EnumPosicao;
import com.alvaro.empresas.passagens.autobuses.enums.EnumTipoBus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
    private int id;

    private int nSillas;
    private int nFilas;
    private int nPiso;
    private int primeraSilla;
    @Enumerated(EnumType.STRING)
    private EnumPosicao posicionPasillo;
    @Enumerated(EnumType.STRING)
    private EnumTipoBus tipo;
    @Enumerated(EnumType.STRING)
    private EnumPosicao inicioContagem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_autobus")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private AutobusModel autobus;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "piso")
    private List<AsientoBloqueadoModel> asientosBloqueados = new ArrayList<AsientoBloqueadoModel>();

    public void llenarSinVector(PisoDTO dto) {
        nSillas = dto.getNSillas();
        nFilas = dto.getNFilas();
        nPiso = dto.getNPiso();
        primeraSilla = dto.getPrimeraSilla();

        switch (dto.getTipo()) {
            case "leito":
                tipo = EnumTipoBus.LEITO;
                dto.setPosicionPasillo("");
                break;
            case "tradicional":
                dto.setPosicionPasillo("medio");
                tipo = EnumTipoBus.TRADICIONAL;
                break;
        }

        switch (dto.getPosicionPasillo()) {
            case "medio":
                posicionPasillo = EnumPosicao.MEDIO;
                break;
            case "izquierda":
                posicionPasillo = EnumPosicao.IZQUIERDA;
                break;
            case "derecha":
                posicionPasillo = EnumPosicao.DERECHA;
                break;
        }

        switch (dto.getInicioContagem()) {
            case "izquierda":
                inicioContagem = EnumPosicao.DERECHA;
                break;
            case "derecha":
                inicioContagem = EnumPosicao.IZQUIERDA;
                break;
        }
    }
}
