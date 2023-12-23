package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.dtos.LayoutBusDTO;
import com.alvaro.empresas.passagens.enums.autobus.EnumPosicao;
import com.alvaro.empresas.passagens.enums.autobus.EnumTipoBus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tb_layout_bus")
@Getter
@Setter
@NoArgsConstructor
public class LayoutBusModel {
    @Id
    @Column(name = "idtb_layout_bus")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int nSillas;
    private int nFilas;
    @Enumerated(EnumType.STRING)
    private EnumPosicao posicionPasillo;
    @Enumerated(EnumType.STRING)
    private EnumTipoBus tipo;
    @Enumerated(EnumType.STRING)
    private EnumPosicao inicioContagem;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "layout")
    private List<AutobusModel> autobuses = new ArrayList<AutobusModel>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "layout")
    private List<AsientoBloqueadoModel> asientosBloqueados = new ArrayList<AsientoBloqueadoModel>();

    public void llenarSinVector(LayoutBusDTO dto) {
        nSillas = dto.getNSillas();
        nFilas = dto.getNFilas();

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

        switch (dto.getTipo()) {
            case "leito":
                tipo = EnumTipoBus.LEITO;
                break;
            case "tradicional":
                tipo = EnumTipoBus.TRADICIONAL;
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
