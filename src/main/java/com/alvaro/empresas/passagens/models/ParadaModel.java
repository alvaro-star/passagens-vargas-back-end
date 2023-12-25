package com.alvaro.empresas.passagens.models;


import com.alvaro.empresas.passagens.lugares.models.LugarModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_parada")
@Getter
@Setter
@NoArgsConstructor
public class ParadaModel {

    @Id
    @Column(name = "idtb_parada", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_lugar")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LugarModel lugar;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_trayecto")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private TrayectoModel trayecto;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "salida")
    private List<ViajeModel> salidas = new ArrayList<ViajeModel>();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "destino")
    private List<ViajeModel> destinos = new ArrayList<ViajeModel>();


}
