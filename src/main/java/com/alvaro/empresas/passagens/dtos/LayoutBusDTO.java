package com.alvaro.empresas.passagens.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LayoutBusDTO {
    private int id;

    @NotNull
    private int nSillas;
    @NotNull
    private int nFilas;
    @NotBlank
    private String posicionPasillo;
    @NotBlank
    private String tipo;
    @NotBlank
    private String inicioContagem;
    private List<AsientoBloqueadoDTO> asientosBloqueados;
}
