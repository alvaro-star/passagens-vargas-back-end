package com.alvaro.empresas.passagens.models;

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
    private Boolean estaPagado = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_trayecto")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private TrayectoModel trayecto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_precio")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private PrecioModel precio;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "pasaje")
    private PasajeroModel pasajero;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_pago")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private PagoModel pago;

    public PasajeModel(Integer nSilla, Boolean compradoWeb, Boolean estaPagado, TrayectoModel trayecto, PrecioModel precio, PagoModel pago) {
        this.nSilla = nSilla;
        this.compradoWeb = compradoWeb;
        this.estaPagado = estaPagado;
        this.trayecto = trayecto;
        this.precio = precio;
        this.pago = pago;
    }
}
