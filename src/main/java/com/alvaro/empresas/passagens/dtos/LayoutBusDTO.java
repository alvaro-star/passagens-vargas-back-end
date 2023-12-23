package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.models.LayoutBusModel;
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
    private List<AsientoBloqueadoDTO> asientosBloqueados = new ArrayList<>();

    public LayoutBusDTO(LayoutBusModel model) {
        id = model.getId();
        nSillas = model.getNSillas();
        nFilas = model.getNFilas();
        posicionPasillo = model.getPosicionPasillo().toString();
        tipo = model.getTipo().toString();
        inicioContagem = model.getInicioContagem().toString();
    }

}
