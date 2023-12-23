package com.alvaro.empresas.passagens.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "tb_pasaje")
@Getter
@Setter
@NoArgsConstructor
public class PasajeModel {
    @Id
    @Column(name = "idtb_pasaje")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String carnet;
    private String nombre;
    @Column(name = "comprado_na_web?")
    private boolean compradoWeb;
    private int numeroAsiento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_autobus")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private AutobusModel autobus;

}
