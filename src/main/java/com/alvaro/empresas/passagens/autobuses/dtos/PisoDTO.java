package com.alvaro.empresas.passagens.autobuses.dtos;

import com.alvaro.empresas.passagens.autobuses.models.PisoModel;
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
public class PisoDTO {

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

    @NotNull
    private Integer idAutobus;

    private List<AsientoBloqueadoDTO> asientosBloqueados = new ArrayList<AsientoBloqueadoDTO>();

    public PisoDTO(PisoModel model) {
        id = model.getId();
        nSillas = model.getNSillas();
        nFilas = model.getNFilas();
        nPiso = model.getNPiso();
        primeraSilla = model.getPrimeraSilla();
        posicionPasillo = model.getPosicionPasillo().toString();
        tipo = model.getTipo().toString();
        inicioContagem = model.getInicioContagem().toString();
    }

}
