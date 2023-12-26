package com.alvaro.empresas.passagens.paradas.dtos;

import com.alvaro.empresas.passagens.paradas.models.CiudadModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CiudadDTO {
    private int id;
    @NotBlank
    private String nombre;
    @NotNull
    private int idDepartamento;

    public CiudadDTO(CiudadModel model) {
        id = model.getId();
        nombre = model.getNombre();
    }
}
