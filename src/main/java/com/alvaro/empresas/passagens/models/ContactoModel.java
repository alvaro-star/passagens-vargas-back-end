package com.alvaro.empresas.passagens.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_contacto")
@Getter
@Setter
@NoArgsConstructor
public class ContactoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtb_contacto")
    private Long id;
    @Column(length = 50)
    private String email;
    private Integer numero;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_pago")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private PasajeModel pago;

    public ContactoModel(String email, Integer numero) {
        this.email = email;
        this.numero = numero;
    }
}
