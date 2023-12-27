package com.alvaro.empresas.passagens.autobuses.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AutobusDTOUpdate {
    private int id;
    @NotBlank
    private String placa;
    @NotNull
    private int idEmpresa;
}
