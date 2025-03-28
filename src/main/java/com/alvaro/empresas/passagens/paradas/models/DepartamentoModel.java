package com.alvaro.empresas.passagens.paradas.models;

import java.util.ArrayList;
import java.util.List;

import com.alvaro.empresas.passagens.paradas.dtos.DepartamentoInputDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;


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
    @JsonIgnore
    private List<CidadeModel> cidades = new ArrayList<>();

    public DepartamentoModel(DepartamentoInputDTO dto) {
        nome = dto.nome().toUpperCase();
        abreviacao = dto.abreviacao().toUpperCase();
    }

    public DepartamentoModel(String nome, String abreviacao) {
        this.nome = nome.toUpperCase();
        this.abreviacao = abreviacao.toUpperCase();
    }

    public void updateValues(DepartamentoInputDTO dto) {
        nome = dto.nome().toUpperCase();
        abreviacao = dto.abreviacao().toUpperCase();
    }
}