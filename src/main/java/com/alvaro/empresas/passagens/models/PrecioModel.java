package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.dtos.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.PrecioDTOUpdate;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_precio")
@Getter
@Setter
@NoArgsConstructor
public class PrecioModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idtb_precio")
    private UUID id;
    @NotNull
    private Float precio;
    @NotNull
    private Integer nPiso;

    private Boolean lleno = false;
    private Integer nSillasDisponibles;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_viaje")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ViajeModel viaje;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "precio")
    private List<SillaModel> sillas = new ArrayList<SillaModel>();

    public PrecioModel(PrecioDTO dto) {
        precio = dto.precio();
        nPiso = dto.nPiso();
    }

    public PrecioModel(Float precio, Integer nPiso, Integer nSillasDisponibles) {
        this.precio = precio;
        this.nPiso = nPiso;
        this.nSillasDisponibles = nSillasDisponibles;
    }

    public void updateValues(PrecioDTOUpdate dto) {
        precio = dto.precio();
    }
}
