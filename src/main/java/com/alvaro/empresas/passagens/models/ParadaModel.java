package com.alvaro.empresas.passagens.models;


import com.alvaro.empresas.passagens.enums.EnumParada;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Table(name = "tb_parada")
@Getter
@Setter
@NoArgsConstructor
public class ParadaModel {

    @Id
    @Column(name = "idtb_parada", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private LocalDateTime dataHora;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnumParada estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "fk_idtb_viaje")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ViajeModel viaje;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "fk_idtb_lugar")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LugarModel lugar;

}
