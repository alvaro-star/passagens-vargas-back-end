package com.alvaro.empresas.passagens.dtos.pasajes;

import com.alvaro.empresas.passagens.models.PasajeModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;

import java.util.Date;
import java.util.UUID;

public record PasajeDTOEmpresaResponse(
        UUID id,
        String carnet,
        String nombre,
        Date nascimento,
        Integer nSilla,
        ParadaDTOComplete salida,
        ParadaDTOComplete destino
) {
    public PasajeDTOEmpresaResponse(PasajeModel model, ParadaDTOComplete salida, ParadaDTOComplete destino){
        this(model.getId(), model.getCarnet(), model.getNombre(), model.getNascimento(), model.getNSilla(), salida, destino);
    }
}
