package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.dtos.EmpresaInputDTO;
import com.alvaro.empresas.passagens.interfaces.IEntityStandart;
import com.alvaro.empresas.passagens.onibus.models.OnibusModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "tb_empresa")
@AttributeOverride(name = "id", column = @Column(name = "idtb_empresa"))
public class EmpresaModel extends IEntityStandart {
    @Column(unique = true, nullable = false)
    private String nome;
    @Column(nullable = false)
    private String logo;
    @Column(nullable = false)
    @JsonIgnore
    private String nConta;
    @Column(nullable = false)
    private Boolean habilitado;
    @Column(nullable = false)
    private Boolean bloqueado;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "empresa")
    @JsonIgnore
    private List<OnibusModel> onibus = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "empresa")
    @JsonIgnore
    private List<ViagemModel> viagens = new ArrayList<>();

    public EmpresaModel(EmpresaInputDTO dto) {
        nome = dto.nome();
        logo = dto.logo();
        nConta = dto.nConta();
    }

    public EmpresaModel(String nome, String logo, String nConta, Boolean habilitado, Boolean bloqueado) {
        this.nome = nome;
        this.logo = logo;
        this.nConta = nConta;
        this.habilitado = habilitado;
        this.bloqueado = bloqueado;
    }
}