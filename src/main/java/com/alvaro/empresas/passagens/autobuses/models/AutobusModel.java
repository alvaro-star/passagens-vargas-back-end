package com.alvaro.empresas.passagens.autobuses.models;

import com.alvaro.empresas.passagens.autobuses.dtos.AutobusDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.AutobusDTOUpdate;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.models.TrayectoModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_autobus")
@Getter
@Setter
@NoArgsConstructor
public class AutobusModel {
    @Id
    @Column(name = "idtb_autobus")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true, nullable = false)
    private String placa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_empresa")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private EmpresaModel empresa;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "autobus")
    private List<PisoModel> pisos = new ArrayList<PisoModel>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "autobus")
    private List<TrayectoModel> trayectos = new ArrayList<TrayectoModel>();

    public AutobusModel(AutobusDTO dto) {
        placa = dto.getPlaca();
    }

    public void updateValues(AutobusDTOUpdate dto) {
        placa = dto.placa();
    }
}
