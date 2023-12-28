package com.alvaro.empresas.passagens.autobuses.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PisoDtoUpdate {

    private Integer id;
    @NotNull
    private Integer nSillas;
    @NotNull
    private Integer nFilas;
    @NotNull
    private Integer nPiso;
    @NotNull
    private Integer primeraSilla;
    @NotBlank
    private String posicionPasillo;
    @NotBlank
    private String tipo;
    @NotBlank
    private String inicioContagem;

    private Integer idAutobus;
    private List<AsientoBloqueadoDTO> asientosBloqueados = new ArrayList<AsientoBloqueadoDTO>();

}
