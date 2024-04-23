package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.dtos.viajes.ViajeDTOList;
import com.alvaro.empresas.passagens.models.TrayectoModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTOComplete;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record TrayectoDTOResponse(
        UUID codigo,
        @NotNull
        Integer idAutobus,
        List<ParadaDTOComplete> paradas,
        List<ViajeDTOList> viajes
        //private List<PasajeDto> pasajes;
) {
    public TrayectoDTOResponse(TrayectoModel model, Integer idAutobus, List<ParadaDTOComplete> paradas, List<ViajeDTOList> viajes) {
        this(model.getCodigo(), idAutobus, paradas, viajes);
    }
}
