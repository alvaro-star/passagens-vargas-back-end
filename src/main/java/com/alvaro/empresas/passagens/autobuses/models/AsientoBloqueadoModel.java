package com.alvaro.empresas.passagens.autobuses.models;


import com.alvaro.empresas.passagens.autobuses.dtos.AsientoBloqueadoDTO;
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
public class AsientoBloqueadoModel {
    @Id
    @Column(name = "idtb_asiento_bloqueado")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int linha;
    private int coluna;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_piso")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private PisoModel piso;

    public AsientoBloqueadoModel(AsientoBloqueadoDTO dto) {
        linha = dto.getLinha();
        coluna = dto.getColuna();
    }
}
