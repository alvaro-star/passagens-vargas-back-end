package com.alvaro.empresas.passagens.autobuses.models;


import com.alvaro.empresas.passagens.autobuses.dtos.PosicionIndisponibleDTO;
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
public class PosicionIndisponibleModel {
    @Id
    @Column(name = "idtb_asiento_bloqueado")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer numero;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fk_idtb_piso")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private PisoModel piso;

    public PosicionIndisponibleModel(PosicionIndisponibleDTO dto) {
        numero = dto.numero();
    }
}
