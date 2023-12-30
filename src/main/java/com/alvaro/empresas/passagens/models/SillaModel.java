package com.alvaro.empresas.passagens.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "tb_sillas")
@Getter
@Setter
@NoArgsConstructor
public class SillaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idtb_silla")
    private UUID id;
    @Column(nullable = false)
    private Integer numero;
    @Column(nullable = false)
    private Integer nPiso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_viaje")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ViajeModel viaje;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "fk_idtb_pasaje")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private PasajeModel pasaje;

    public SillaModel(Integer numero, Integer nPiso, ViajeModel viaje) {
        this.numero = numero;
        this.nPiso = nPiso;
        this.viaje = viaje;
    }

    public SillaModel(Integer numero, Integer nPiso, ViajeModel viaje, PasajeModel pasaje) {
        this.numero = numero;
        this.nPiso = nPiso;
        this.viaje = viaje;
        this.pasaje = pasaje;
    }
}
