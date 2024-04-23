package com.alvaro.empresas.passagens.paradas.models;

import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOUpdate;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_parada", indexes = @Index(name = "idx_dataHora", columnList = "data_hora"))
@Getter
@Setter
@NoArgsConstructor
public class ParadaModel {

    @Id
    @Column(name = "idtb_parada", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, name = "data_hora")
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private int plataforma;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_lugar")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LugarModel lugar;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_viaje")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ViajeModel viaje;

    public ParadaModel(ParadaDTO dto) {
        dataHora = dto.dataHora();
        plataforma = dto.plataforma();
    }

    public ParadaModel(LocalDateTime dataHora, int plataforma, LugarModel lugar, ViajeModel viaje) {
        this.dataHora = dataHora;
        this.plataforma = plataforma;
        this.lugar = lugar;
        this.viaje = viaje;
    }

    public void updateValues(ParadaDTOUpdate dtoUpdate) {
        dataHora = dtoUpdate.dataHora();
        if (dtoUpdate.plataforma() != null) {
            plataforma = dtoUpdate.plataforma();
        }
    }
}
