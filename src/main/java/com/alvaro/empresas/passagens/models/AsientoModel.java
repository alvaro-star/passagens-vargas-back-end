package com.alvaro.empresas.passagens.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;

@Entity
@Table(name = "tb_asiento")
@Getter
@Setter
@NoArgsConstructor
public class AsientoModel implements Serializable {

    @Id
    @Column(name = "idtb_asieto")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int numero;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "fk_idtb_autobus")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private AutobusModel autobus;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "asiento")
    private ArrayList<PasajeModel> pasajes = new ArrayList<PasajeModel>();
}
