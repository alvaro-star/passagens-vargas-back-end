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
@Table(name = "tb_pasaje")
@Getter
@Setter
@NoArgsConstructor
public class PasajeModel {
    @Id
    @Column(name = "idtb_pasaje")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String carnet;
    private String nombre;
    @Column(name = "comprado_na_web?")
    private Boolean compradoWeb;

    private Date nascimento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_trayecto")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private TrayectoModel trayecto;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "pasaje")
    private SillaModel silla;

    //Comprador
    //Pagos

    public PasajeModel(PasajeDTO dto) {
        carnet = dto.carnet();
        nombre = dto.nombre();
        nascimento = dto.nascimento();
    }
}
