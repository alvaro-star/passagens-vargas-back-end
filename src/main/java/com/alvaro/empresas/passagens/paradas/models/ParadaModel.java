package com.alvaro.empresas.passagens.paradas.models;

import com.alvaro.empresas.passagens.enums.EnumParada;
import com.alvaro.empresas.passagens.models.EmpresaModel;
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
@Table(name = "tb_parada", indexes = {
        @Index(name = "idxtb_parada_fk_idtb_lugar_dataHora", columnList = "fk_idtb_lugar, data_hora"),
        @Index(name = "idxtb_parada_fk_idtb_empresa_fk_idtb_lugar_dataHora", columnList = "fk_idtb_empresa, fk_idtb_lugar, data_hora"),
        @Index(name = "idxtb_parada_fk_idtb_viaje", columnList = "fk_idtb_viaje")
})
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
    @Enumerated(EnumType.STRING)
    private EnumParada tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_lugar")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LugarModel lugar;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_viaje")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ViajeModel viaje;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_empresa")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private EmpresaModel empresa;

    public ParadaModel(ParadaDTO dto, EnumParada tipo) {
        dataHora = dto.dataHora();
        this.tipo = tipo;
        plataforma = dto.plataforma();
    }

    public ParadaModel(LocalDateTime dataHora, int plataforma, EnumParada tipo, LugarModel lugar, ViajeModel viaje, EmpresaModel empresa) {
        this.dataHora = dataHora;
        this.plataforma = plataforma;
        this.tipo = tipo;
        this.lugar = lugar;
        this.viaje = viaje;
        this.empresa = empresa;
    }

    public void updateValues(ParadaDTOUpdate dtoUpdate) {
        dataHora = dtoUpdate.dataHora();
        if (dtoUpdate.plataforma() != null) {
            plataforma = dtoUpdate.plataforma();
        }
    }
}
