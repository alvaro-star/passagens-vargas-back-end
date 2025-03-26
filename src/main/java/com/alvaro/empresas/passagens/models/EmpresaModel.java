package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.onibus.models.AutobusModel;
import com.alvaro.empresas.passagens.dtos.EmpresaDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "tb_empresa")
@AttributeOverride(name = "id", column = @Column(name = "idtb_empresa"))
public class EmpresaModel extends IEntityStandart {
    @Column(unique = true, nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String logo;
    @Column(nullable = false)
    private String numeroCuenta;
    @Column(nullable = false)
    private Boolean enabled;
    @Column(nullable = false)
    private Boolean bloqued;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "empresa")
    private List<AutobusModel> autobuses = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "empresa")
    private List<ViagemModel> viajes = new ArrayList<>();

    public EmpresaModel(EmpresaDTO dto) {
        nombre = dto.nombre();
        logo = dto.logo();
        numeroCuenta = dto.numeroCuenta();
    }

    public EmpresaModel(String nombre, String logo, String numeroCuenta, Boolean enabled, Boolean bloqued) {
        this.nombre = nombre;
        this.logo = logo;
        this.numeroCuenta = numeroCuenta;
        this.enabled = enabled;
        this.bloqued = bloqued;
    }
}
