package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.models.EmpresaModel;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpresaDto {

    private int id;
    @NotBlank
    private String nombre;
    @NotBlank
    private String logo;
    @NotBlank
    private String numeroCuenta;

    public EmpresaDto(String nombre, String logo, String numeroCuenta) {
        this.nombre = nombre;
        this.logo = logo;
        this.numeroCuenta = numeroCuenta;
    }

    public EmpresaDto(EmpresaModel model) {
        id = model.getId();
        nombre = model.getNombre();
        logo = model.getLogo();
        numeroCuenta = model.getNumeroCuenta();
    }
}
