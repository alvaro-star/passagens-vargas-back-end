package com.alvaro.empresas.passagens.paradas.models;

import com.alvaro.empresas.passagens.paradas.dtos.DepartamentoDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tb_departamento")
@Data
@NoArgsConstructor
public class DepartamentoModel {
    @Id
    @Column(name = "idtb_departamento")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    @Column(unique = true)
    private String nome;
    @NotBlank
    @Column(length = 4)
    private String abreviacao;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "departamento")
    private List<CidadeModel> cidades = new ArrayList<>();

    public DepartamentoModel(DepartamentoDTO dto) {
        nome = dto.nome().toUpperCase();
        abreviacao = dto.abreviacao().toUpperCase();
    }

    public DepartamentoModel(String nome, String abreviacao) {
        this.nome = nome.toUpperCase();
        this.abreviacao = abreviacao.toUpperCase();
    }

    public void updateValues(DepartamentoDTO dto) {
        nome = dto.nome().toUpperCase();
        abreviacao = dto.abreviacao().toUpperCase();
    }
}