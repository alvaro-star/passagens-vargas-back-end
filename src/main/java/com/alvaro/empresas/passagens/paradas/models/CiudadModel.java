package com.alvaro.empresas.passagens.paradas.models;

import com.alvaro.empresas.passagens.paradas.dtos.CiudadDTO;
import com.alvaro.empresas.passagens.paradas.dtos.CiudadDtoUpdate;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_ciudad", indexes = @Index(name = "idxtb_ciudad_nombre", columnList = "nombre"))
@Getter
@Setter
@NoArgsConstructor
public class CiudadModel {
    @Id
    @Column(name = "idtb_ciudad")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank
    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_idtb_departamento")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private DepartamentoModel departamento;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "ciudad")
    private List<LugarModel> lugares = new ArrayList<>();

    public CiudadModel(CiudadDTO dto) {
        nombre = dto.nombre().toUpperCase();
    }

    public void updateValues(CiudadDtoUpdate dto) {
        nombre = dto.nombre().toUpperCase();
    }

    public CiudadModel(String nombre, DepartamentoModel departamento) {
        this.nombre = nombre.toUpperCase();
        this.departamento = departamento;
    }
}
