package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.dtos.pasagens.ContactoDTO;
import com.alvaro.empresas.passagens.pagamentos.models.FaturaPasagemModel;
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
    private String nombre;
    @Column(length = 50)
    private String email;
    private Integer numero;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_factura_pasaje")
    private FaturaPasagemModel faturaPasagem;

    public ContatoModel(ContactoDTO contactoDTO) {
        this.nombre = contactoDTO.nombre();
        this.email = contactoDTO.email();
        this.numero = contactoDTO.telefono();
    }

    public ContatoModel(String nombre, String email, Integer numero) {
        this.nombre = nombre;
        this.email = email;
        this.numero = numero;
    }
}
