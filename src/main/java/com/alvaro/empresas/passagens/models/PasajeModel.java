package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.dtos.pasajes.PasajeDTO;
import com.alvaro.empresas.passagens.enums.TipoPagamento;
import com.alvaro.empresas.passagens.pagos.models.FacturaPasajeModel;
import com.alvaro.empresas.passagens.pagos.models.FacturaRembolsoModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
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

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_idtb_factura_rembolso")
    private FacturaRembolsoModel facturaRembolso;
    @Column(name = "fk_idtb_factura_rembolso", insertable = false, updatable = false)
    private UUID facturaRembolsoId;

    @Column(nullable = false)
    private Boolean enEfectivo;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoPagamento metodoPago;

    @Column(nullable = false, length = 9)
    private String carnet;
    @Column(nullable = false, length = 70)
    private String nombre;
    @Column(nullable = false)
    private Date nascimento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_id_salida")
    private ParadaModel salida;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_id_destino")
    private ParadaModel destino;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_idtb_precio")
    private PrecioModel precio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_factura_pasaje")
    private FacturaPasajeModel facturaPasaje;
    @Column(name = "fk_idtb_factura_pasaje", updatable = false, insertable = false)
    private UUID facturaPasajeId;

    public PasajeModel(PasajeDTO pasajeDTO, Boolean compradoWeb, BigDecimal precioPagado, Boolean estaPagado, Boolean enEfectivo, ParadaModel salida, ParadaModel destino, PrecioModel precio, FacturaPasajeModel facturaPasaje) {
        this.nSilla = pasajeDTO.nSilla();
        this.carnet = pasajeDTO.carnet();
        this.nombre = pasajeDTO.nombre();
        this.nascimento = pasajeDTO.nascimento();

        this.precioPagado = precioPagado;
        this.compradoWeb = compradoWeb;
        this.estaPagado = estaPagado;
        this.facturaRembolso = null;
        this.facturaRembolsoId = null;
        this.enEfectivo = enEfectivo;

        this.facturaPasaje = facturaPasaje;
        if (facturaPasaje != null) {
            this.metodoPago = facturaPasaje.getMetodoPago();
            this.facturaPasajeId = facturaPasaje.getId();
        }

        this.salida = salida;
        this.destino = destino;
        this.precio = precio;
    }

    public PasajeModel(Integer nSilla, Boolean compradoWeb, BigDecimal precioPagado, Boolean estaPagado, Boolean enEfectivo, String nombre, String carnet, Date nascimento, ParadaModel salida, ParadaModel destino, PrecioModel precio, FacturaPasajeModel facturaPasaje) {
        this.nSilla = nSilla;
        this.compradoWeb = compradoWeb;
        this.estaPagado = estaPagado;
        this.enEfectivo = enEfectivo;
        this.carnet = carnet;
        this.nombre = nombre.toUpperCase();
        this.nascimento = nascimento;
        this.precioPagado = precioPagado;
        this.precio = precio;
        this.facturaPasaje = facturaPasaje;
        this.metodoPago = facturaPasaje.getMetodoPago();
        this.salida = salida;
        this.destino = destino;
        this.facturaRembolso = null;
        this.facturaRembolsoId = null;
    }
}
