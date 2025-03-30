package com.alvaro.empresas.passagens.paradas.models;

import java.util.ArrayList;
import java.util.List;

import com.alvaro.empresas.passagens.paradas.dtos.LugarCreateDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "tb_lugar")
@Data
@NoArgsConstructor
public class LugarModel {
    @Id
    @Column(name = "idtb_lugar")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank
    private String nome;
    @Column(nullable = false)
    private Boolean enabled = true;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_idtb_cidade")
    @JsonIgnore
    private CidadeModel cidade;

    @Column(name = "fk_idtb_cidade", insertable = false, updatable = false)
    private Integer cidadeId;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "lugar")
    @JsonIgnore
    private List<ParadaModel> paradas = new ArrayList<>();

    public LugarModel(LugarCreateDTO dto, CidadeModel cidade) {
        nome = dto.nome().toUpperCase();
        setCidade(cidade);
    }

    public LugarModel(String nome, CidadeModel cidade) {
        this.nome = nome.toUpperCase();
        setCidade(cidade);
    }

    public void setCidade(CidadeModel cidade) {
        this.cidade = cidade;
        this.cidadeId = (cidade.getId() != null) ? cidade.getId() : null;
    }
}