package com.alvaro.empresas.passagens.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;


@Entity
@Table(name = "tb_lugar")
@Getter
@Setter
@NoArgsConstructor
public class LugarModel {

    @Id
    @Column(name = "idtb_lugar")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String nombre;
    private String departamento;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lugar")
    private ArrayList<ParadaModel> paradas = new ArrayList<ParadaModel>();


}
