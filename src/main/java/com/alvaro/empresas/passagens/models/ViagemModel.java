package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.enums.TipoParada;
import com.alvaro.empresas.passagens.interfaces.IEntityStandart;
import com.alvaro.empresas.passagens.onibus.models.OnibusModel;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaPassagemModel;
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
        indexes = @Index(name = "idxtb_viagem_fk_idtb_empresa_data_hora_saida", columnList = "fk_idtb_empresa, data_hora_saida")
)
@NoArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "idtb_viagem"))
public class ViagemModel extends IEntityStandart {
    @Column(precision = 10, scale = 2, nullable = false)
    @DecimalMin("0.00")
    private BigDecimal valorArrecadadoDinheiro;

    @Column(precision = 10, scale = 2, nullable = false)
    @DecimalMin("0.00")
    private BigDecimal valorArrecadadoNaoWeb;

    @Column(precision = 10, scale = 2, nullable = false)
    @DecimalMin("0.00")
    private BigDecimal valorArrecadadoWeb;

    @Column(name = "cobrado", nullable = false)
    private boolean isCobrado;
    @Column(name = "cancelado", nullable = false)
    private boolean isCancelado = false;

    @Column(nullable = false, name = "data_hora_saida")
    private LocalDateTime dataHoraSaida;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_onibus")
    private OnibusModel onibus;
    @Column(name = "fk_idtb_onibus", updatable = false, insertable = false)
    private UUID onibusId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_empresa")
    private EmpresaModel empresa;
    @Column(name = "fk_idtb_empresa", insertable = false, updatable = false)
    private UUID empresaId;

    public void setOnibus(OnibusModel onibus) {
        this.onibus = onibus;
        this.onibusId = (onibus != null) ? onibus.getId() : null;
    }

    public void setEmpresa(EmpresaModel empresa) {
        this.empresa = empresa;
        this.empresaId = (empresa != null) ? empresa.getId() : null;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "viagem")
    private List<ParadaModel> paradas = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "viagem")
    private List<FaturaPassagemModel> faturasPassagens = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "viagem")
    private List<PrecoModel> precos = new ArrayList<>();

    public void addParada(ParadaModel parada) {
        parada.setViagem(this);
        this.paradas.add(parada);
    }

    public void addPreco(PrecoModel preco) {
        preco.setViagem(this);
        this.precos.add(preco);
    }

    public ViagemModel(OnibusModel onibus, EmpresaModel empresa, BigDecimal valorArrecadadoDinheiro, BigDecimal valorArrecadadoNaoWeb, BigDecimal valorArrecadadoWeb, boolean isCobrado, LocalDateTime dataHoraSaida) {
        setOnibus(onibus);
        this.valorArrecadadoDinheiro = valorArrecadadoDinheiro;
        this.valorArrecadadoNaoWeb = valorArrecadadoNaoWeb;
        this.valorArrecadadoWeb = valorArrecadadoWeb;
        this.isCobrado = isCobrado;
        setEmpresa(empresa);
        this.dataHoraSaida = dataHoraSaida;
    }

    public ViagemModel(OnibusModel onibus, LocalDateTime dataHoraSaida) {
        valorArrecadadoDinheiro = BigDecimal.ZERO;
        valorArrecadadoNaoWeb = BigDecimal.ZERO;
        valorArrecadadoWeb = BigDecimal.ZERO;
        isCobrado = false;
        isCancelado = false;
        this.dataHoraSaida = dataHoraSaida;
        setOnibus(onibus);
        setEmpresa(empresa);
        this.paradas = new ArrayList<>();
    }

    public BigDecimal addValorArrecadadoWeb(BigDecimal valor) {
        if (valorArrecadadoWeb == null) valorArrecadadoWeb = valor;
        else valorArrecadadoWeb = valorArrecadadoWeb.add(valor);
        return valorArrecadadoWeb;
    }

    public BigDecimal addValorArrecadadoNaoWeb(BigDecimal valor) {
        if (valorArrecadadoNaoWeb == null) valorArrecadadoNaoWeb = valor;
        else valorArrecadadoNaoWeb = valorArrecadadoNaoWeb.add(valor);
        return valorArrecadadoNaoWeb;
    }

    public BigDecimal addValorArrecadadoDinheiro(BigDecimal valor) {
        if (valorArrecadadoDinheiro == null) valorArrecadadoDinheiro = valor;
        else valorArrecadadoDinheiro = valorArrecadadoDinheiro.add(valor);
        return valorArrecadadoDinheiro;
    }

    public boolean subtrairValorDinheiro(BigDecimal valor) {
        int comparacao = valorArrecadadoDinheiro.compareTo(valor);
        if (comparacao < 0) return false;
        this.valorArrecadadoDinheiro = valorArrecadadoDinheiro.subtract(valor);
        return true;
    }

    public boolean subtrairValorWeb(BigDecimal valor) {
        int comparacao = valorArrecadadoWeb.compareTo(valor);
        if (comparacao < 0) return false;
        this.valorArrecadadoWeb = valorArrecadadoWeb.subtract(valor);
        return true;
    }

    public boolean subtrairValorNaoWeb(BigDecimal valor) {
        int comparacao = valorArrecadadoNaoWeb.compareTo(valor);
        if (comparacao < 0) return false;
        this.valorArrecadadoNaoWeb = valorArrecadadoNaoWeb.subtract(valor);
        return true;
    }

    public ParadaModel getParadaByLugarId(Integer idLugar) {
        for (ParadaModel parada : this.getParadas()) {
            if (parada.getLugar().getId().equals(idLugar))
                return parada;
        }
        return null;
    }

    public ParadaModel getSaida() {
        for (ParadaModel parada : this.paradas)
            if (parada.getTipo().equals(TipoParada.SAIDA))
                return parada;
        return null;
    }

    public ParadaModel getDestino() {
        for (ParadaModel parada : this.paradas)
            if (parada.getTipo().equals(TipoParada.DESTINO))
                return parada;
        return null;
    }


    public PrecoModel getPrecoByNPiso(Integer nPiso) {
        for (PrecoModel preco : this.precos) {
            if (preco.getNPiso().equals(nPiso))
                return preco;
        }
        return null;
    }


}