package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
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

    @Column(nullable = false)
    private Integer nSilla;
    @Column(name = "comprado_na_web", nullable = false)
    private Boolean compradoWeb;
    @Column(name = "pagado", nullable = false)
    private Boolean estaPagado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_id_salida")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ParadaModel salida;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_id_destino")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ParadaModel destino;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_precio")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private PrecioModel precio;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "pasaje")
    private PasajeroModel pasajero;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_pago")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private PagoModel pago;

    public PasajeModel(Integer nSilla, Boolean compradoWeb, Boolean estaPagado, ParadaModel salida, ParadaModel destino, PrecioModel precio, PagoModel pago, PasajeroModel pasajero) {
        this.nSilla = nSilla;
        this.compradoWeb = compradoWeb;
        this.estaPagado = estaPagado;
        this.precio = precio;
        this.pago = pago;
        this.pasajero = pasajero;
    }
}
