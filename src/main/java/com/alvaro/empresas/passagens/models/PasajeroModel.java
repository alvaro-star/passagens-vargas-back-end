package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.dtos.pasajes.PasajeDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "tb_pasajero")
@Getter
@Setter
@NoArgsConstructor
public class PasajeroModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idtb_pasajero")
    private UUID id;

    @Column(nullable = false)
    private String carnet;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private Date nascimento;


    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_pasaje")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private PasajeModel pasaje;

    public PasajeroModel(PasajeDTO dto) {
        carnet = dto.carnet();
        nombre = dto.nombre();
        nascimento = dto.nascimento();
    }
}
