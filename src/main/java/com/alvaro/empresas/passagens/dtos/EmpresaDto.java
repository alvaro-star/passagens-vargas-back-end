package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.models.EmpresaModel;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class EmpresaDto {

    private int id;
    @NotBlank
    private String nombre;
    @NotBlank
    private String logo;
    @NotBlank
    private String numeroCuenta;

    public EmpresaDto(EmpresaModel model) {
        id = model.getId();
        nombre = model.getNombre();
        logo = model.getLogo();
        numeroCuenta = model.getNumeroCuenta();
    }
}
