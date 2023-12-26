package com.alvaro.empresas.passagens.paradas.models;

import com.alvaro.empresas.passagens.paradas.dtos.LugarDTO;
import com.alvaro.empresas.passagens.models.ParadaModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


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
    @NotBlank
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_ciudad")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private CiudadModel ciudad;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "lugar")
    private List<ParadaModel> paradas = new ArrayList<ParadaModel>();

    public LugarModel(LugarDTO dto) {
        nombre = dto.getNombre();
    }

}
