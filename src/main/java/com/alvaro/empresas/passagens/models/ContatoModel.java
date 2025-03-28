package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.dtos.pasagens.ContatoDTO;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaPassagemModel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "tb_contato")
@NoArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "idtb_contato"))
public class ContatoModel extends IEntityStandart {
    @Column(length = 70)
    private String nome;
    @Column(length = 50)
    private String email;
    private Integer numero;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_factura_pasaje")
    private FaturaPassagemModel faturaPasagem;

    public ContatoModel(ContatoDTO contatoDTO) {
        this.nome = contatoDTO.nome();
        this.email = contatoDTO.email();
        this.numero = contatoDTO.telefone();
    }

    public ContatoModel(String nome, String email, Integer numero) {
        this.nome = nome;
        this.email = email;
        this.numero = numero;
    }
}