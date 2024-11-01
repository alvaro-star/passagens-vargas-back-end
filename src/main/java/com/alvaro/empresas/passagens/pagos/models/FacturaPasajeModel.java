package com.alvaro.empresas.passagens.pagos.models;

import com.alvaro.empresas.passagens.enums.TipoPagamentoEnum;
import com.alvaro.empresas.passagens.models.ContactoModel;
import com.alvaro.empresas.passagens.models.PasajeModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.alvaro.empresas.passagens.security.models.UsuarioModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tb_factura_pasaje", indexes = @Index(name = "idxtb_viaje_fk_idtb_viaje_created_at", columnList = "fk_idtb_viaje, created_at"))
@DiscriminatorValue("PASAJE")
public class FacturaPasajeModel extends FacturaModel {
    @Column(nullable = false)
    private BigDecimal descuento;
    @Column(nullable = false)
    private BigDecimal tasaServicio;
    @Column(nullable = false, name = "pagado?")
    private Boolean estaPagado;
    @Column(nullable = false, name = "metodo_pago")
    @Enumerated(EnumType.STRING)
    private TipoPagamentoEnum metodoPago;
    private LocalDateTime fechaPago;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "facturaPasaje")
    private ContactoModel contacto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_idtb_cliente")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UsuarioModel cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_idtb_viaje")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ViajeModel viaje;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "facturaPasaje")
    private List<PasajeModel> pasajes;

    public FacturaPasajeModel(BigDecimal valorTotal, BigDecimal descuento, BigDecimal tasaServicio, Boolean estaPagado, TipoPagamentoEnum metodoPago, ViajeModel viajeModel, LocalDateTime fechaPago, ContactoModel contacto) {
        super(valorTotal);
        this.descuento = descuento;
        this.tasaServicio = tasaServicio;
        this.estaPagado = estaPagado;
        this.metodoPago = metodoPago;
        this.fechaPago = fechaPago;
        this.viaje = viajeModel;
        this.contacto = contacto;
    }
}
