package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tb_viaje")
@Getter
@Setter
@NoArgsConstructor
public class ViajeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtb_viaje")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_salida")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ParadaModel salida;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_destino")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ParadaModel destino;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_trayecto")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private TrayectoModel trayecto;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "viaje")
    private List<PrecioModel> precios = new ArrayList<>();

    public ViajeModel(ParadaModel salida, ParadaModel destino, TrayectoModel trayecto) {
        this.salida = salida;
        this.destino = destino;
        this.trayecto = trayecto;
    }
}
