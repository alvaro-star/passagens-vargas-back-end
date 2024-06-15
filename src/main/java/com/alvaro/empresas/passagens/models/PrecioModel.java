package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.dtos.precios.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.precios.PrecioDTOUpdate;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
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

    @Column(precision = 10, scale = 2, nullable = false)
    @DecimalMin("0.00")
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer nPiso;

    @Column(nullable = false)
    private Boolean lleno = false;

    @Column(nullable = false)
    private Integer nSillasDisponibles;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_viaje")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ViajeModel viaje;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_empresa")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private EmpresaModel empresa;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "precio")
    private List<PasajeModel> pasajes = new ArrayList<>();

    public PrecioModel(PrecioDTO dto) {
        precio = dto.precio();
        nPiso = dto.nPiso();
    }

    public PrecioModel(BigDecimal precio, Integer nPiso, Integer nSillasDisponibles) {
        this.precio = precio;
        this.nPiso = nPiso;
        this.nSillasDisponibles = nSillasDisponibles;
    }

    public PrecioModel(BigDecimal precio, Integer nPiso, Integer nSillasDisponibles, ViajeModel viaje, EmpresaModel empresa) {
        this.precio = precio;
        this.nPiso = nPiso;
        this.nSillasDisponibles = nSillasDisponibles;
        this.viaje = viaje;
        this.empresa = empresa;
    }

    public void updateValues(PrecioDTOUpdate dto) {
        precio = dto.precio();
    }
}
