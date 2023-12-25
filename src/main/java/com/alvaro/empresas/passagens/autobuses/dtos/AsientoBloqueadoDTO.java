package com.alvaro.empresas.passagens.autobuses.dtos;

import com.alvaro.empresas.passagens.autobuses.models.AsientoBloqueadoModel;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AsientoBloqueadoDTO {
    private int id;
    @NotBlank
    private int linha;
    @NotBlank
    private int coluna;

    public AsientoBloqueadoDTO(AsientoBloqueadoModel model) {
        id = model.getId();
        linha = model.getLinha();
        coluna = model.getColuna();
    }
}
