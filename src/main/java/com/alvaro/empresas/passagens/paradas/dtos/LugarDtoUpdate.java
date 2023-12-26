package com.alvaro.empresas.passagens.paradas.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LugarDtoUpdate {
    private int id;
    @NotBlank
    private String nombre;
}
