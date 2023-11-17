package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.security.models.UserModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Entity
@Table(name = "tb_empresa")
@Getter
@Setter
public class EmpresaModel {
    @Id
    @Column(name = "idtb_empresa")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true, nullable = false)
    private String nombre;
    private String logo;
    private String numeroCuenta;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "empresa")
    private ArrayList<AutobusModel> autobuses = new ArrayList<AutobusModel>();
}
