package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.enums.TipoParada;
import com.alvaro.empresas.passagens.onibus.models.AutobusModel;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaPasagemModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_viagem",
        indexes = @Index(name = "idxtb_viagem_fk_idtb_empresa_data_hora_salida", columnList = "fk_idtb_empresa, data_hora_salida")
)
@NoArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "idtb_viagem"))
public class ViagemModel extends IEntityStandart {
    @Column(precision = 10, scale = 2, nullable = false)
    @DecimalMin("0.00")
    private BigDecimal valorArrecadadoEfectivo;

    @Column(precision = 10, scale = 2, nullable = false)
    @DecimalMin("0.00")
    private BigDecimal valorArrecadadoNoWeb;

    @Column(precision = 10, scale = 2, nullable = false)
    @DecimalMin("0.00")
    private BigDecimal valorArrecadadoWeb;

    @Column(name = "cobrado", nullable = false)
    private boolean isCobrado;
    @Column(name = "cancelado", nullable = false)
    private boolean isCancelado = false;

    @Column(nullable = false, name = "data_hora_salida")
    private LocalDateTime dataHoraSalida;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_autobus")
    private AutobusModel autobus;
    @Column(name = "fk_idtb_autobus", updatable = false, insertable = false)
    private Integer autobusId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_empresa")
    private EmpresaModel empresa;
    @Column(name = "fk_idtb_empresa", insertable = false, updatable = false)
    private UUID empresaId;

    public void setAutobus(AutobusModel autobus) {
        this.autobus = autobus;
        if (autobus != null)
            autobusId = autobus.getId();
    }

    public void setEmpresa(EmpresaModel empresa) {
        this.empresa = empresa;
        if (empresa != null)
            empresaId = empresa.getId();
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "viagem")
    private List<ParadaModel> paradas = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "viagem")
    private List<FaturaPasagemModel> facturasPasajes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "viagem")
    private List<PrecoModel> precios = new ArrayList<>();

    public void addParada(ParadaModel parada) {
        parada.setviagem(this);
        this.paradas.add(parada);
    }

    public void addPrecio(PrecoModel precio) {
        precio.setViagem(this);
        this.precios.add(precio);
    }

    public ViagemModel(AutobusModel autobus, EmpresaModel empresa, BigDecimal valorArrecadadoEfectivo, BigDecimal valorArrecadadoNoWeb, BigDecimal valorArrecadadoWeb, boolean isCobrado, LocalDateTime dataHoraSalida) {
        this.autobus = autobus;
        this.autobusId = autobus.getId();
        this.valorArrecadadoEfectivo = valorArrecadadoEfectivo;
        this.valorArrecadadoNoWeb = valorArrecadadoNoWeb;
        this.valorArrecadadoWeb = valorArrecadadoWeb;
        this.isCobrado = isCobrado;
        this.empresa = empresa;
        this.empresaId = empresa.getId();
        this.dataHoraSalida = dataHoraSalida;
    }

    public ViagemModel(AutobusModel autobus, LocalDateTime dataHoraSalida) {
        valorArrecadadoEfectivo = BigDecimal.ZERO;
        valorArrecadadoNoWeb = BigDecimal.ZERO;
        valorArrecadadoWeb = BigDecimal.ZERO;
        isCobrado = false;
        isCancelado = false;
        this.dataHoraSalida = dataHoraSalida;
        this.autobus = autobus;
        this.autobusId = autobus.getId();
        this.empresa = autobus.getEmpresa();
        this.empresaId = autobus.getEmpresaId();
        this.paradas = new ArrayList<>();
    }

    public BigDecimal addValorArrecadadoWeb(BigDecimal valor) {
        if (valorArrecadadoWeb == null) valorArrecadadoWeb = valor;
        else valorArrecadadoWeb = valorArrecadadoWeb.add(valor);
        return valorArrecadadoWeb;
    }

    public BigDecimal addValorArrecadadoNoWeb(BigDecimal valor) {
        if (valorArrecadadoNoWeb == null) valorArrecadadoNoWeb = valor;
        else valorArrecadadoNoWeb = valorArrecadadoNoWeb.add(valor);
        return valorArrecadadoNoWeb;
    }

    public BigDecimal addValorArrecadadoEfectivo(BigDecimal valor) {
        if (valorArrecadadoEfectivo == null) valorArrecadadoEfectivo = valor;
        else valorArrecadadoEfectivo = valorArrecadadoEfectivo.add(valor);
        return valorArrecadadoEfectivo;
    }

    public boolean substractValueEfectivo(BigDecimal valor) {
        int comparacao = valorArrecadadoEfectivo.compareTo(valor);
        if (comparacao < 0) return false;
        this.valorArrecadadoEfectivo = valorArrecadadoEfectivo.subtract(valor);
        return true;
    }

    public boolean substractValueWeb(BigDecimal valor) {
        int comparacao = valorArrecadadoWeb.compareTo(valor);
        if (comparacao < 0) return false;
        this.valorArrecadadoWeb = valorArrecadadoWeb.subtract(valor);
        return true;
    }

    public boolean substractValueNoWeb(BigDecimal valor) {
        int comparacao = valorArrecadadoNoWeb.compareTo(valor);
        if (comparacao < 0) return false;
        this.valorArrecadadoNoWeb = valorArrecadadoNoWeb.subtract(valor);
        return true;
    }

    public ParadaModel getParadaByLugarId(Integer idLugar) {
        for (ParadaModel parada : this.getParadas()) {
            if (parada.getLugar().getId().equals(idLugar))
                return parada;
        }
        return null;
    }

    public ParadaModel getSalida() {
        for (ParadaModel parada : this.paradas)
            if (parada.getTipo().equals(TipoParada.SALIDA))
                return parada;
        return null;
    }

    public ParadaModel getDestino() {
        for (ParadaModel parada : this.paradas)
            if (parada.getTipo().equals(TipoParada.DESTINO))
                return parada;
        return null;
    }


    public PrecoModel getPrecioByNPiso(Integer nPiso) {
        for (PrecoModel precio : this.precios) {
            if (precio.getNPiso().equals(nPiso))
                return precio;
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
