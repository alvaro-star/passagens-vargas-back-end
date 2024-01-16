package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.autobuses.models.AutobusModel;
import com.alvaro.empresas.passagens.dtos.EmpresaDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_empresa")
@Getter
@Setter
public class EmpresaModel {
    @Id
    @Column(name = "idtb_empresa")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String nombre;
    private String logo;
    private String numeroCuenta;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "empresa")
    private List<AutobusModel> autobuses = new ArrayList<AutobusModel>();

    //Funcionarios
    public EmpresaModel() {
    }

    public EmpresaModel(EmpresaDto dto) {
        nombre = dto.nombre();
        logo = dto.logo();
        numeroCuenta = dto.numeroCuenta();
    }


}
