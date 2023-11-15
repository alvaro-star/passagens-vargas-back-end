package com.alvaro.empresas.passagens.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cliente {

    @NotBlank
    private String nombre;
    private String carnet;
    private String email;

    public Cliente(String nombre, String carnet, String email) {
        this.nombre = nombre;
        this.carnet = carnet;
        this.email = email;
    }
}
