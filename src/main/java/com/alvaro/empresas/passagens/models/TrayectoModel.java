package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_trayecto")
@Setter
@Getter
@NoArgsConstructor
public class TrayectoModel {

    @Id
    @Column(name = "idtb_trayecto")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID codigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_autobus")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private AutobusModel autobus;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "trayecto")
    private List<ParadaModel> paradas = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "trayecto")
    private List<ViajeModel> viajes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "trayecto")
    private List<PagoModel> pagos = new ArrayList<>();

    public ParadaModel getParadaById(Integer id) {
        for (ParadaModel parada : this.paradas) {
            if (parada.getId().equals(id)) {
                return parada;
            }
        }
        return null;
    }

    public TrayectoModel(AutobusModel autobus) {
        this.autobus = autobus;
    }

    public ParadaModel getParadaByLugarId(Integer idLugar) {
        for (ParadaModel parada : this.getParadas()) {
            if (parada.getLugar().getId() == idLugar) {
                return parada;
            }
        }
        return null;
    }

    public boolean dataHoraValido(LocalDateTime dtoTime) {
        if (this.getParadas().size() >= 2) {
            LocalDateTime maior = this.getParadas().get(0).getDataHora();
            LocalDateTime menor = this.getParadas().get(0).getDataHora();
            for (ParadaModel parada : this.getParadas()) {
                if (parada.getDataHora().isAfter(maior)) {
                    maior = parada.getDataHora();
                }
                if (parada.getDataHora().isBefore(menor)) {
                    menor = parada.getDataHora();
                }
            }
            if (dtoTime.isAfter(menor) && dtoTime.isBefore(maior)) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public boolean maiorDataHoraParada(LocalDateTime time) {
        for (ParadaModel parada : this.paradas) {
            if (time.isBefore(parada.getDataHora())) {
                return false;
            }
        }
        return true;
    }

    public ParadaModel getMenorParada() {
        if (paradas.isEmpty()) {
            return null;
        }
        int indiceMenor = 0;
        for (int i = 0; i < paradas.size(); i++) {
            if (paradas.get(indiceMenor).getDataHora().isAfter(paradas.get(i).getDataHora())) {
                indiceMenor = i;
            }
        }
        return paradas.get(indiceMenor);
    }

    public ParadaModel getMaiorParada() {
        if (paradas.isEmpty()) {
            return null;
        }
        int indiceMaior = 0;
        for (int i = 0; i < paradas.size(); i++) {
            if (paradas.get(indiceMaior).getDataHora().isBefore(paradas.get(i).getDataHora())) {
                indiceMaior = i;
            }
        }
        return paradas.get(indiceMaior);
    }

}
