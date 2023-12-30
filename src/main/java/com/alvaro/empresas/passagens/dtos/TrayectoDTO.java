package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.models.TrayectoModel;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TrayectoDTO {

    private UUID codigo;
    @NotNull
    private Integer idAutobus;

    public TrayectoDTO(TrayectoModel model) {
        codigo = model.getCodigo();
    }
}
