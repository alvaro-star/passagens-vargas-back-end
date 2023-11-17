package com.alvaro.empresas.passagens.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "tb_asiento_bloqueado")
@Getter
@Setter
@NoArgsConstructor
public class AsientoBoqueadoModel {
    @Id
    @Column(name = "idtb_asiento_bloqueado")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int linha;
    private int coluna;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "fk_idtb_layout_bus")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LayoutBusModel layout;
}
