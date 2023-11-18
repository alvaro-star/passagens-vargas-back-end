package com.alvaro.empresas.passagens.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;


@Entity
@Table(name = "tb_viaje")
@Getter
@Setter
@NoArgsConstructor
public class ViajeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtb_viaje")
    private int id;
    @Column(nullable = false)
    private float precio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_autobus")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private AutobusModel autobus;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "viaje")
    private ArrayList<ParadaModel> paradas = new ArrayList<ParadaModel>();

}
