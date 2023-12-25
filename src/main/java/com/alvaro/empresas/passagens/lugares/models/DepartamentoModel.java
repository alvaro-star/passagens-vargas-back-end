package com.alvaro.empresas.passagens.lugares.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tb_departamento")
@Getter
@Setter
@NoArgsConstructor
public class DepartamentoModel {
    @Id
    @Column(name = "idtb_departamento")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank
    private String nombre;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "departamento")
    private List<CiudadModel> ciudades = new ArrayList<>();
}
