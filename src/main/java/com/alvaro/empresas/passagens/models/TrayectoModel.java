package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private List<ParadaModel> paradas = new ArrayList<ParadaModel>();

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "trayecto")
    private List<ViajeModel> viajes = new ArrayList<ViajeModel>();

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "trayecto")
    private List<PasajeModel> pasajes = new ArrayList<PasajeModel>();

    public ParadaModel getParadaById(Integer id) {
        for (ParadaModel parada : this.paradas) {
            if (parada.getId() == id) {
                return parada;
            }
        }
        return null;
    }

    public boolean posseeViaje(Integer idSalida, Integer idDestino) {
        for (ViajeModel viaje : this.viajes) {
            if (viaje.getSalida().getId() == idSalida && viaje.getDestino().getId() == idDestino) {
                return true;
            }
        }
        return false;
    }

}
