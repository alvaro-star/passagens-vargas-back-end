package com.alvaro.empresas.passagens.paradas.dtos;

import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class LugarDTO {
    private int id;
    @NotBlank
    private String nombre;
    @NotNull
    private int idCiudad;

    public LugarDTO(LugarModel model) {
        nombre = model.getNombre();
    }
}
