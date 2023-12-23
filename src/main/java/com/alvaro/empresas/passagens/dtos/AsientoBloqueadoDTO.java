package com.alvaro.empresas.passagens.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsientoBloqueadoDTO {
    private int id;
    @NotBlank
    private int linha;
    @NotBlank
    private int coluna;
}
