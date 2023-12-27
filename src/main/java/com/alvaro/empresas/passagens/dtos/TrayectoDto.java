package com.alvaro.empresas.passagens.dtos;

import com.alvaro.empresas.passagens.models.TrayectoModel;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TrayectoDto {
    private UUID codigo;
    @NotBlank
    private int idAutobus;

    public TrayectoDto(TrayectoModel model) {
        codigo = model.getCodigo();
    }
}
