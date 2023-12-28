package com.alvaro.empresas.passagens.autobuses.dtos;

import com.alvaro.empresas.passagens.autobuses.models.AsientoBloqueadoModel;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AsientoBloqueadoDTO {
    private Integer id;
    @NotNull
    private Integer linha;
    @NotNull
    private Integer coluna;

    public AsientoBloqueadoDTO(AsientoBloqueadoModel model) {
        id = model.getId();
        linha = model.getLinha();
        coluna = model.getColuna();
    }
}
