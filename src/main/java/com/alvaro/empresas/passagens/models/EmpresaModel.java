package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.dtos.EmpresaDto;
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
@Table(name = "tb_empresa")
@Getter
@Setter
@NoArgsConstructor
public class EmpresaModel {
    @Id
    @Column(name = "idtb_empresa")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "empresa")
    private List<AutobusModel> autobuses = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "empresa")
    private List<ViajeModel> viajes = new ArrayList<>();

    //Funcionarios
    public EmpresaModel(EmpresaDto dto) {
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
