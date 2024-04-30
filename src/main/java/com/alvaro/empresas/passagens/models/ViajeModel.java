package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "tb_viaje")
@Getter
@Setter
@NoArgsConstructor
public class ViajeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idtb_viaje")
    private UUID codigo;

    @Column(precision = 10, scale = 2, nullable = false)
    @DecimalMin("0.00")
    private BigDecimal valorArrecadadoEfectivo;

    @Column(precision = 10, scale = 2, nullable = false)
    @DecimalMin("0.00")
    private BigDecimal valorArrecadadoWeb;

    @Column(name = "cobrado", nullable = false)
    private boolean isCobrado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_autobus")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private AutobusModel autobus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_empresa")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private EmpresaModel empresa;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "viaje")
    private List<ParadaModel> paradas = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "viaje")
    private List<PagoModel> pagos = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "viaje")
    private List<PrecioModel> precios = new ArrayList<>();

    public ViajeModel(AutobusModel autobus, EmpresaModel empresa, BigDecimal valorArrecadadoEfectivo, BigDecimal valorArrecadadoWeb, boolean isCobrado) {
        this.autobus = autobus;
        this.valorArrecadadoEfectivo = valorArrecadadoEfectivo;
        this.valorArrecadadoWeb = valorArrecadadoWeb;
        this.isCobrado = isCobrado;
        this.empresa = empresa;
    }


    public ParadaModel getParadaByLugarId(Integer idLugar) {
        for (ParadaModel parada : this.getParadas()) {
            if (parada.getLugar().getId() == idLugar)
                return parada;
        }
        return null;
    }

    public boolean dataHoraValido(LocalDateTime dtoTime) {
        if (this.getParadas().size() >= 2) {
            LocalDateTime maior = this.getParadas().get(0).getDataHora();
            LocalDateTime menor = this.getParadas().get(0).getDataHora();
            for (ParadaModel parada : this.getParadas()) {
                if (parada.getDataHora().isAfter(maior))
                    maior = parada.getDataHora();

                if (parada.getDataHora().isBefore(menor))
                    menor = parada.getDataHora();
            }
            return dtoTime.isAfter(menor) && dtoTime.isBefore(maior);
        }
        return true;
    }

}
