package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.dtos.precos.PrecioDTO;
import com.alvaro.empresas.passagens.dtos.precos.PrecioDTOUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_preco")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "idtb_preco"))
public class PrecoModel extends IEntityStandart {
    @Column(precision = 10, scale = 2, nullable = false)
    @DecimalMin("0.00")
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer nPiso;

    @Column(nullable = false)
    private Boolean lleno = false;

    @Column(nullable = false)
    private Integer nSillasDisponibles;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_viagem")
    private ViagemModel viagem;
    @Column(name = "fk_idtb_viagem", updatable = false, insertable = false)
    private UUID viagemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_empresa")
    private EmpresaModel empresa;
    @Column(name = "fk_idtb_empresa", updatable = false, insertable = false)
    private UUID empresaId;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "preco")
    private List<PassagemModel> pasajes = new ArrayList<>();

    public void setViagem(ViagemModel viagem) {
        this.viagem = viagem;
        if (viagem != null)
            viagemId = viagem.getId();
    }

    public void setEmpresa(EmpresaModel empresa) {
        this.empresa = empresa;
        empresaId = (empresa.getId() != null) ? empresa.getId() : null;
    }

    public PrecoModel(PrecioDTO dto) {
        precio = dto.precio();
        nPiso = dto.nPiso();
    }

    public PrecoModel(BigDecimal precio, Integer nPiso, Integer nSillasDisponibles) {
        this.precio = precio;
        this.nPiso = nPiso;
        this.nSillasDisponibles = nSillasDisponibles;
    }

    public PrecoModel(BigDecimal precio, Integer nPiso, Integer nSillasDisponibles, ViagemModel viagem) {
        this.precio = precio;
        this.nPiso = nPiso;
        this.nSillasDisponibles = nSillasDisponibles;
        setViagem(viagem);
        this.empresaId = viagem.getEmpresaId();
        this.empresa = viagem.getEmpresa();
    }

    public void updateValues(PrecioDTOUpdate dto) {
        precio = dto.precio();
    }
}
