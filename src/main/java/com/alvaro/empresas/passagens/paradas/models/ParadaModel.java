package com.alvaro.empresas.passagens.paradas.models;

import com.alvaro.empresas.passagens.enums.TipoParada;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOUpdate;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_parada", indexes = {
        @Index(name = "idxtb_parada_fk_idtb_lugar_dataHora", columnList = "fk_idtb_lugar, data_hora"),
        @Index(name = "idxtb_parada_fk_idtb_empresa_fk_idtb_lugar_dataHora", columnList = "fk_idtb_empresa, fk_idtb_lugar, data_hora"),
        @Index(name = "idxtb_parada_fk_idtb_viagem", columnList = "fk_idtb_viagem")
})
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
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
    private TipoParada tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_lugar")
    private LugarModel lugar;
    @Column(name = "fk_idtb_lugar", updatable = false, insertable = false)
    private Integer lugarId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_viagem")
    private ViagemModel viagem;
    @Column(name = "fk_idtb_viagem", insertable = false, updatable = false)
    private UUID viagemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_empresa")
    private EmpresaModel empresa;
    @Column(name = "fk_idtb_empresa", insertable = false, updatable = false)
    private UUID empresaId;

    public ParadaModel(ParadaDTO dto, TipoParada tipo) {
        dataHora = dto.dataHora();
        this.tipo = tipo;
        plataforma = dto.plataforma();
    }

    public ParadaModel(LocalDateTime dataHora, int plataforma, TipoParada tipo, LugarModel lugar, ViagemModel viagem) {
        this.dataHora = dataHora;
        this.plataforma = plataforma;
        this.tipo = tipo;
        this.lugar = lugar;
        this.lugarId = lugar.getId();
        this.viagem = viagem;
        this.viagemId = viagem.getId();
        this.empresa = viagem.getEmpresa();
        this.empresaId = viagem.getEmpresaId();
    }

    public void setLugar(LugarModel lugar) {
        this.lugar = lugar;
        this.lugarId = lugar.getId();

    }

    public void setviagem(ViagemModel viagem) {
        this.viagem = viagem;
        this.viagemId = (viagem != null) ? viagem.getId() : null;
    }

    public void setEmpresa(EmpresaModel empresa) {
        this.empresa = empresa;
        if (empresa != null) this.empresaId = empresa.getId();
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
