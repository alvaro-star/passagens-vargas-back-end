package com.alvaro.empresas.passagens.onibus.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.interfaces.IEntityStandart;
import com.alvaro.empresas.passagens.models.ViagemModel;
import com.alvaro.empresas.passagens.onibus.dtos.onibus.OnibusCreateDTO;
import com.alvaro.empresas.passagens.onibus.dtos.onibus.OnibusUpdateDTO;

import com.alvaro.empresas.passagens.onibus.dtos.pisos.PisoCreateDTO;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_onibus", indexes = {
        @Index(name = "idxtb_onibus_empresa_criado", columnList = "fk_idtb_empresa, created_at"),
        @Index(name = "idxtb_onibus_placa", columnList = "placa")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "idtb_onibus"))
public class OnibusModel extends IEntityStandart {
    @Column(nullable = false)
    private String placa;

    @Column(nullable = false)
    private boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_empresa")
    private EmpresaModel empresa;

    @Column(name = "fk_idtb_empresa", insertable = false, updatable = false)
    private UUID empresaId;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "onibus")
    private List<PisoModel> pisos = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "onibus")
    private List<ViagemModel> viagens = new ArrayList<>();

    public void setEmpresa(EmpresaModel empresa) {
        this.empresa = empresa;
        this.empresaId = (empresa != null) ? empresa.getId() : null;
    }

    public OnibusModel(OnibusCreateDTO dto, EmpresaModel empresa) {
        placa = dto.placa();
        enabled = true;
        setEmpresa(empresa);
    }

    public void addPiso(PisoModel piso) {
        piso.setOnibus(this);
        pisos.add(piso);
    }

    public void updateValues(OnibusUpdateDTO dto) {
        placa = dto.placa();
    }

    public PisoModel getPisoByNumero(Integer nPiso) {
        for (PisoModel piso : this.pisos)
            if (piso.getNPiso().equals(nPiso))
                return piso;
        return null;
    }
}