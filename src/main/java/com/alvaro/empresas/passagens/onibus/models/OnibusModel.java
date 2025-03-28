package com.alvaro.empresas.passagens.onibus.models;

import com.alvaro.empresas.passagens.models.IEntityStandart;
import com.alvaro.empresas.passagens.onibus.dtos.onibus.OnibusDTO;
import com.alvaro.empresas.passagens.onibus.dtos.onibus.OnibusDTOUpdate;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private boolean habilitado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_empresa")
    private EmpresaModel empresa;

    @Column(name = "fk_idtb_empresa", insertable = false, updatable = false)
    private UUID empresaId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "autobus")
    private List<PisoModel> pisos = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "autobus")
    private List<ViagemModel> viagens = new ArrayList<>();

    public void setEmpresa(EmpresaModel empresa) {
        this.empresa = empresa;
        this.empresaId = (empresa != null) ? empresa.getId() : null;
    }

    public OnibusModel(OnibusDTO dto, EmpresaModel empresa) {
        placa = dto.placa();
        habilitado = true;
        setEmpresa(empresa);
    }

    public void updateValues(OnibusDTOUpdate dto) {
        placa = dto.placa();
    }

    public PisoModel getPisoByNumero(Integer nPiso) {
        for (PisoModel piso : this.pisos)
            if (piso.getNPiso().equals(nPiso))
                return piso;
        return null;
    }
}