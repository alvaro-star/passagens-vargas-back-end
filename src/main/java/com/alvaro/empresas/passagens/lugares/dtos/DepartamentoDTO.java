package com.alvaro.empresas.passagens.lugares.dtos;

import com.alvaro.empresas.passagens.lugares.models.DepartamentoModel;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepartamentoDTO {
    private int id;
    @NotBlank(message = "Escriba un nombre valido")
    private String nombre;

    public DepartamentoDTO(DepartamentoModel model) {
        id = model.getId();
        nombre = model.getNombre();
    }
}
