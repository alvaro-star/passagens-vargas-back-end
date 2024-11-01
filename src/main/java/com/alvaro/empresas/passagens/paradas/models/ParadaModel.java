package com.alvaro.empresas.passagens.paradas.models;

import com.alvaro.empresas.passagens.enums.EnumParada;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOUpdate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

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
    private LugarModel lugar;
    @Column(name = "fk_idtb_lugar", updatable = false, insertable = false)
    private Integer lugarId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_viaje")
    private ViajeModel viaje;
    @Column(name = "fk_idtb_viaje", insertable = false, updatable = false)
    private UUID viajeCodigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_empresa")
    private EmpresaModel empresa;
    @Column(name = "fk_idtb_empresa", insertable = false, updatable = false)
    private UUID empresaId;

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
        this.lugarId = lugar.getId();
        this.viaje = viaje;
        this.viajeCodigo = viaje.getCodigo();
        this.empresa = empresa;
        this.empresaId = empresa.getId();
    }

    public void setLugar(LugarModel lugar) {
        this.lugar = lugar;
        this.lugarId = lugar.getId();

    }

    public void setViaje(ViajeModel viaje) {
        this.viaje = viaje;
        this.viajeCodigo = viaje.getCodigo();
    }

    public void setEmpresa(EmpresaModel empresa) {
        this.empresa = empresa;
        this.empresaId = empresa.getId();
    }

    public void updateValues(ParadaDTOUpdate dtoUpdate) {
        dataHora = dtoUpdate.dataHora();
        if (dtoUpdate.plataforma() != null)
            plataforma = dtoUpdate.plataforma();
    }

    public String toStringCiudadDepartamentoFormat() {
        return lugar.getCiudad().getNombre() + " - " + lugar.getCiudad().getDepartamento().getAbreviacion();
    }
}
