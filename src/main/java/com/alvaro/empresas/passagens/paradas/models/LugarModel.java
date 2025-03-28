package com.alvaro.empresas.passagens.paradas.models;

import com.alvaro.empresas.passagens.paradas.dtos.LugarDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


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
    private Boolean habilitado = true;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_idtb_cidade")
    private CidadeModel cidade;

    @Column(name = "fk_idtb_cidade", insertable = false, updatable = false)
    private Integer cidadeId;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "lugar")
    private List<ParadaModel> paradas = new ArrayList<>();

    public LugarModel(LugarDTO dto, CidadeModel cidade) {
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