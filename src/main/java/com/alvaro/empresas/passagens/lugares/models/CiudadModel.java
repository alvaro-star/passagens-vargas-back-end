package com.alvaro.empresas.passagens.lugares.models;

import com.alvaro.empresas.passagens.lugares.dtos.CiudadDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_ciudad")
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_departamento")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private DepartamentoModel departamento;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "ciudad")
    private List<LugarModel> lugares = new ArrayList<>();

    public CiudadModel(CiudadDTO dto) {
        nombre = dto.getNombre();
    }
}
