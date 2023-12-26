package com.alvaro.empresas.passagens.lugares.dtos;

import com.alvaro.empresas.passagens.lugares.models.CiudadModel;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CiudadDtoUpdate {
    private int id;
    @NotBlank
    private String nombre;

    public CiudadDtoUpdate(CiudadModel model) {
        id = model.getId();
        nombre = model.getNombre();
    }
}
