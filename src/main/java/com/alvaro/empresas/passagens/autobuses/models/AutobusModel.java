package com.alvaro.empresas.passagens.autobuses.models;

import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTOUpdate;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_autobus", indexes = @Index(name="idxtb_autobus_empresa_criado", columnList = "fk_idtb_empresa, created_at"))
@Getter
@Setter
@NoArgsConstructor
//@Table(name = "tb_parada", indexes = @Index(name = "idx_dataHora", columnList = "data_hora"))
//indexes = { @Index(name = "idx_composto", columnList = "chave_estrangeira, chave_unica")
public class AutobusModel {
    @Id
    @Column(name = "idtb_autobus")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String placa;

    @Column(nullable = false)
    private boolean enable = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_empresa")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private EmpresaModel empresa;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "autobus")
    private List<PisoModel> pisos = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "autobus")
    private List<ViajeModel> viajes = new ArrayList<>();

    public AutobusModel(String placa, EmpresaModel empresa) {
        this.placa = placa;
        this.empresa = empresa;
    }

    public PisoModel getPisoByNumero(Integer nPiso) {
        for (PisoModel piso : this.pisos)
            if (piso.getNPiso().equals(nPiso))
                return piso;
        return null;
    }

    public AutobusModel(AutobusDTO dto) {
        placa = dto.placa();
        enable = true;
    }

    public void updateValues(AutobusDTOUpdate dto) {
        placa = dto.placa();
        if (dto.enable() != null) {
            enable = dto.enable();
        }
    }
}
