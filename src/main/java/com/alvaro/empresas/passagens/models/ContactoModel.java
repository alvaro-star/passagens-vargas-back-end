package com.alvaro.empresas.passagens.models;

import com.alvaro.empresas.passagens.dtos.pasajes.ContactoDTO;
import com.alvaro.empresas.passagens.pagos.models.FacturaPasajeModel;
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
    @Column(length = 70)
    private String nombre;
    @Column(length = 50)
    private String email;
    private Integer numero;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_factura_pasaje")
    private FacturaPasajeModel facturaPasaje;

    public ContactoModel(ContactoDTO contactoDTO) {
        this.nombre = contactoDTO.nombre();
        this.email = contactoDTO.email();
        this.numero = contactoDTO.telefono();
    }

    public ContactoModel(String nombre, String email, Integer numero) {
        this.nombre = nombre;
        this.email = email;
        this.numero = numero;
    }
}
