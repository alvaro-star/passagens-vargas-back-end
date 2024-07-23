package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;


@Entity
@Table(name = "tb_pasaje", indexes = {
        @Index(name = "idxtb_pasaje_fk_idtb_precio", columnList = "fk_idtb_precio"),
        @Index(name = "idxtb_pasaje_fk_idtb_factura_pasaje", columnList = "fk_idtb_factura_pasaje")
})
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

    @Column(nullable = false)
    private BigDecimal precioPagado;


    @Column(name = "comprado_na_web", nullable = false)
    private Boolean compradoWeb;
    @Column(name = "pagado", nullable = false)
    private Boolean estaPagado;
    @Column(name = "rembolsado", nullable = false)
    private Boolean fueRembolsado = false;
    @Column(nullable = false)
    private Boolean enEfectivo;

    @Column(nullable = false, length = 9)
    private String carnet;
    @Column(nullable = false, length = 70)
    private String nombre;
    @Column(nullable = false)
    private Date nascimento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_id_salida")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ParadaModel salida;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_id_destino")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ParadaModel destino;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_idtb_precio")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private PrecioModel precio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_factura_pasaje")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private FacturaPasajeModel facturaPasaje;

    public PasajeModel(Integer nSilla, Boolean compradoWeb, BigDecimal precioPagado, Boolean estaPagado, Boolean enEfectivo, String nombre, String carnet, Date nascimento, ParadaModel salida, ParadaModel destino, PrecioModel precio, FacturaPasajeModel facturaPasaje) {
        this.nSilla = nSilla;
        this.compradoWeb = compradoWeb;
        this.estaPagado = estaPagado;
        this.enEfectivo = enEfectivo;
        this.carnet = carnet;
        this.nombre = nombre;
        this.nascimento = nascimento;
        this.precioPagado = precioPagado;
        this.precio = precio;
        this.facturaPasaje = facturaPasaje;
        this.salida = salida;
        this.destino = destino;
    }
}
