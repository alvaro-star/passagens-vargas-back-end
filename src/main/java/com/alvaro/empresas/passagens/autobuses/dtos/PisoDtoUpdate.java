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

    private int id;
    @NotNull
    private int nSillas;
    @NotNull
    private int nFilas;
    @NotNull
    private int nPiso;
    @NotNull
    private int primeraSilla;
    @NotBlank
    private String posicionPasillo;
    @NotBlank
    private String tipo;
    @NotBlank
    private String inicioContagem;

    private int idAutobus;
    private List<AsientoBloqueadoDTO> asientosBloqueados = new ArrayList<AsientoBloqueadoDTO>();

}
