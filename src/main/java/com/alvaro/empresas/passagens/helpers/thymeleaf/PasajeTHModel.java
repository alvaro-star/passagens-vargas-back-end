package com.alvaro.empresas.passagens.helpers.thymeleaf;

import com.alvaro.empresas.passagens.models.PasajeModel;

public record PasajeTHModel(
        Integer nsilla,
        String carnet,
        String nombre,
        String nascimento,
        String salida,
        String destino
) {
    public PasajeTHModel(PasajeModel model) {
        this(
                model.getNSilla(),
                model.getCarnet(),
                model.getNombre(),
                model.getNascimento().toString(),
                model.getSalida().toStringCiudadDepartamentoFormat(),
                model.getDestino().toStringCiudadDepartamentoFormat()
        );
    }
}
