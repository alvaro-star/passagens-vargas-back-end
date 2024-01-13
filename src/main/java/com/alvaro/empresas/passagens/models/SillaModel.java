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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_viaje")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private PrecioModel precio;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "fk_idtb_pasaje")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private PasajeModel pasaje;

    public SillaModel(Integer numero, PrecioModel precio) {
        this.numero = numero;
        this.precio = precio;
    }

    public SillaModel(Integer numero, PrecioModel precio, PasajeModel pasaje) {
        this.numero = numero;
        this.precio = precio;
        this.pasaje = pasaje;
    }
}
