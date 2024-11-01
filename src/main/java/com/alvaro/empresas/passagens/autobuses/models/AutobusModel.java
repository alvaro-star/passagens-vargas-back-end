package com.alvaro.empresas.passagens.autobuses.models;

import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTO;
import com.alvaro.empresas.passagens.autobuses.dtos.autobuses.AutobusDTOUpdate;
import com.alvaro.empresas.passagens.models.EmpresaModel;
import com.alvaro.empresas.passagens.models.ViajeModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_autobus", indexes = {
        @Index(name = "idxtb_autobus_empresa_criado", columnList = "fk_idtb_empresa, created_at"),
        @Index(name = "idxtb_autobus_placa", columnList = "placa")
})
@Getter
@Setter
@NoArgsConstructor
public class AutobusModel {
    @Id
    @Column(name = "idtb_autobus")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String placa;

    @Column(nullable = false)
    private boolean enable;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_empresa")
    private EmpresaModel empresa;

    @Column(name = "fk_idtb_empresa", insertable = false, updatable = false)
    private UUID empresaId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "autobus")
    private List<PisoModel> pisos = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "autobus")
    private List<ViajeModel> viajes = new ArrayList<>();

    public AutobusModel(String placa, Boolean enable, EmpresaModel empresa) {
        this.placa = placa;
        this.empresa = empresa;
        this.empresaId = empresa.getId();
        this.enable = enable;
    }

    public void setEmpresa(EmpresaModel empresa) {
        this.empresa = empresa;
        this.empresaId = empresa.getId();
    }

    public AutobusModel(AutobusDTO dto) {
        placa = dto.placa();
        enable = true;
    }

    public void updateValues(AutobusDTOUpdate dto) {
        placa = dto.placa();
    }


    public PisoModel getPisoByNumero(Integer nPiso) {
        for (PisoModel piso : this.pisos)
            if (piso.getNPiso().equals(nPiso))
                return piso;
        return null;
    }
}
