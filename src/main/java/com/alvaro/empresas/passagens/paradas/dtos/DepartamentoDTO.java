package com.alvaro.empresas.passagens.paradas.dtos;

import com.alvaro.empresas.passagens.paradas.models.DepartamentoModel;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DepartamentoDTO {
    private Integer id;
    @NotBlank(message = "Escriba un nombre valido")
    private String nombre;
    private List<CiudadDTO> ciudades;

    public DepartamentoDTO(DepartamentoModel model) {
        id = model.getId();
        nombre = model.getNombre();
    }
}
