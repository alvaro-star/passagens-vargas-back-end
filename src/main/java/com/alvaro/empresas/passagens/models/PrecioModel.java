package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.dtos.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.PrecioDTOUpdate;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_viaje")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ViajeModel viaje;

    public PrecioModel(PrecioDTO dto) {
        precio = dto.precio();
        nPiso = dto.nPiso();
    }

    public PrecioModel(Float precio, Integer nPiso, ViajeModel viaje) {
        this.precio = precio;
        this.nPiso = nPiso;
        this.viaje = viaje;
    }

    public void updateValues(PrecioDTOUpdate dto) {
        precio = dto.precio();
    }
}
