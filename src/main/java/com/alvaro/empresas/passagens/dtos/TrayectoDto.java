package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.models.TrayectoModel;
import com.alvaro.empresas.passagens.paradas.dtos.ParadaDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TrayectoDto {

    private UUID codigo;
    @NotNull
    private Integer idAutobus;

    private List<ParadaDTO> paradas = new ArrayList<>();

    /*
    private List<PasajeDto> pasajes;
    private List<ViajeDto> viajes;

    */
    public TrayectoDto(TrayectoModel model) {
        codigo = model.getCodigo();
    }
}
