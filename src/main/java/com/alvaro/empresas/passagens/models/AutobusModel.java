package com.alvaro.empresas.passagens.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

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
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_layout_bus")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LayoutBusModel layout;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_empresa")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private EmpresaModel empresa;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "autobus")
    private ArrayList<ViajeModel> viajes = new ArrayList<ViajeModel>();

}
