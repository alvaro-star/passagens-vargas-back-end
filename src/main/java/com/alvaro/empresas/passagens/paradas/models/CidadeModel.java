package com.alvaro.empresas.passagens.paradas.models;

import com.alvaro.empresas.passagens.paradas.dtos.CidadeDTO;
import com.alvaro.empresas.passagens.paradas.dtos.CidadeDTOUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_cidade", indexes = @Index(name = "idxtb_cidade_nome", columnList = "nome"))
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class CidadeModel {
    @Id
    @Column(name = "idtb_cidade")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank
    private String nome;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "fk_idtb_departamento")
    private DepartamentoModel departamento;

    @Column(name = "fk_idtb_departamento", insertable = false, updatable = false)
    private Integer departamentoId;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "cidade")
    private List<LugarModel> lugares = new ArrayList<>();

    public CidadeModel(CidadeDTO dto, DepartamentoModel departamento) {
        nome = dto.nome().toUpperCase();
        setDepartamento(departamento);
    }
    public CidadeModel(String nome, DepartamentoModel departamento) {
        this.nome = nome.toUpperCase();
        setDepartamento(departamento);
    }

    public void setDepartamento(DepartamentoModel departamento) {
        this.departamento = departamento;
        this.departamentoId = (departamento != null) ? departamento.getId() : null;
    }

    public void updateValues(CidadeDTOUpdate dto) {
        nome = dto.nome().toUpperCase();
    }


}