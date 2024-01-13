package com.alvaro.empresas.passagens.paradas.models;


import com.alvaro.empresas.passagens.models.TrayectoModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOUpdate;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_parada", indexes = @Index(name = "idx_dataHora", columnList = "data_hora"))
@Getter
@Setter
@NoArgsConstructor
public class ParadaModel {

    @Id
    @Column(name = "idtb_parada", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, name = "data_hora")
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private int plataforma;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_lugar")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LugarModel lugar;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_trayecto")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private TrayectoModel trayecto;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "salida")
    private List<ViajeModel> salidas = new ArrayList<ViajeModel>();

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "destino")
    private List<ViajeModel> destinos = new ArrayList<ViajeModel>();

    public ParadaModel(ParadaDTO dto) {
        dataHora = dto.dataHora();
        plataforma = dto.plataforma();
    }

    public void updateValues(ParadaDTOUpdate dtoUpdate) {
        dataHora = dtoUpdate.dataHora();
        if (dtoUpdate.plataforma() != null) {
            plataforma = dtoUpdate.plataforma();
        }

    }
}
